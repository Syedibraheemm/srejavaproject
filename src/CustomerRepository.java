import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class CustomerRepository {
  private final MongoCollection<Document> collection;

  public CustomerRepository(){
    collection = MongoConfig.getDatabase().getCollection("customers");
  }

  public boolean checkUser(Long phone){
    return collection.find(Filters.eq("phone", phone)).first() != null;
  }

  public boolean createUser(Long phone){
    if (checkUser(phone)) return true;
    Document doc = new Document()
        .append("phone", phone)
        .append("rentals", new ArrayList<Document>());
    collection.insertOne(doc);
    return true;
  }

  public List<ReturnItem> getOutstandingReturns(Long phone){
    List<ReturnItem> returnList = new ArrayList<ReturnItem>();
    Document customer = collection.find(Filters.eq("phone", phone)).first();
    if (customer == null) return returnList;
    List<Document> rentals = (List<Document>) customer.get("rentals");
    if (rentals == null) return returnList;
    Calendar now = Calendar.getInstance();
    for (Document rental : rentals){
      boolean returned = rental.getBoolean("returned", false);
      if (!returned){
        Integer itemId = rental.getInteger("itemId");
        Date rentalDate = rental.getDate("rentalDate");
        Calendar rentalCal = Calendar.getInstance();
        rentalCal.setTime(rentalDate);
        int days = daysBetween(rentalCal, now);
        returnList.add(new ReturnItem(itemId, days));
      }
    }
    return returnList;
  }

  public void addRental(long phone, List<Item> rentalList){
    createUser(phone);
    List<Document> newRentals = new ArrayList<Document>();
    Date now = new Date();
    for (Item item : rentalList){
      Document rentalDoc = new Document()
          .append("itemId", item.getItemID())
          .append("quantity", item.getAmount())
          .append("rentalDate", now)
          .append("returned", false);
      newRentals.add(rentalDoc);
    }
    collection.updateOne(Filters.eq("phone", phone), Updates.pushEach("rentals", newRentals));
  }

  public void updateRentalStatus(long phone, List<ReturnItem> returnedList){
    Document customer = collection.find(Filters.eq("phone", phone)).first();
    if (customer == null) return;
    List<Document> rentals = (List<Document>) customer.get("rentals");
    if (rentals == null) return;
    Date now = new Date();
    for (ReturnItem ret : returnedList){
      for (Document rental : rentals){
        if (!rental.getBoolean("returned", false) && rental.getInteger("itemId") == ret.getItemID()){
          rental.put("returned", true);
          rental.put("returnedDate", now);
          break;
        }
      }
    }
    collection.updateOne(Filters.eq("phone", phone), Updates.set("rentals", rentals));
  }

  private int daysBetween(Calendar start, Calendar end){
    Calendar dayOne = (Calendar) start.clone();
    Calendar dayTwo = (Calendar) end.clone();
    if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
      return (dayTwo.get(Calendar.DAY_OF_YEAR) - dayOne.get(Calendar.DAY_OF_YEAR));
    } else {
      if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
        Calendar temp = dayOne;
        dayOne = dayTwo;
        dayTwo = temp;
      }
      int extraDays = 0;
      int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);
      while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
        dayOne.add(Calendar.YEAR, -1);
        extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
      }
      return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
    }
  }
}

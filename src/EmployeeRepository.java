import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class EmployeeRepository {
  private final MongoCollection<Document> collection;

  public EmployeeRepository(){
    collection = MongoConfig.getDatabase().getCollection("employees");
  }

  public List<Employee> findAll(){
    List<Employee> list = new ArrayList<Employee>();
    for (Document doc : collection.find()){
      list.add(mapToEmployee(doc));
    }
    return list;
  }

  public Employee findByUsername(String username){
    Document doc = collection.find(Filters.eq("username", username)).first();
    if (doc == null) return null;
    return mapToEmployee(doc);
  }

  public void add(String name, String password, boolean isCashier){
    String username = getNextUsername();
    String[] parts = name.split(" ",2);
    String first = parts.length>0?parts[0]:"";
    String last = parts.length>1?parts[1]:"";
    Document doc = new Document()
        .append("username", username)
        .append("firstName", first)
        .append("lastName", last)
        .append("position", isCashier ? "Cashier" : "Admin")
        .append("password", password);
    collection.insertOne(doc);
  }

  public boolean delete(String username){
    return collection.deleteOne(Filters.eq("username", username)).getDeletedCount() > 0;
  }

  public int update(String username, String password, String position, String name){
    Document doc = collection.find(Filters.eq("username", username)).first();
    if (doc == null) return -1;
    if (!position.equals("") && !(position.equals("Admin")||position.equals("Cashier"))){
      return -2;
    }
    List<org.bson.conversions.Bson> updates = new ArrayList<org.bson.conversions.Bson>();
    if (!password.equals("")) updates.add(Updates.set("password", password));
    if (!position.equals("")) updates.add(Updates.set("position", position));
    if (!name.equals("")){
      String[] parts = name.split(" ",2);
      if (parts.length>0) updates.add(Updates.set("firstName", parts[0]));
      if (parts.length>1) updates.add(Updates.set("lastName", parts[1]));
    }
    if (!updates.isEmpty()){
      collection.updateOne(Filters.eq("username", username), Updates.combine(updates));
    }
    return 0;
  }

  private String getNextUsername(){
    List<Employee> all = findAll();
    int max = all.stream()
      .map(Employee::getUsername)
      .filter(u -> u.matches("\\d+"))
      .map(Integer::parseInt)
      .max(Comparator.naturalOrder())
      .orElse(110000);
    return Integer.toString(max+1);
  }

  private Employee mapToEmployee(Document doc){
    String username = doc.getString("username");
    String position = doc.getString("position");
    String first = doc.getString("firstName");
    String last = doc.getString("lastName");
    String password = doc.getString("password");
    String fullName = (first==null?"":first)+" "+(last==null?"":last);
    return new Employee(username, fullName.trim(), position, password);
  }
}

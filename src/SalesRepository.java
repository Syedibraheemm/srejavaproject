import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class SalesRepository {
  private final MongoCollection<Document> collection;

  public SalesRepository(){
    collection = MongoConfig.getDatabase().getCollection("sales");
  }

  public void logSale(Date date, List<Item> items, double totalWithTax){
    List<Document> lineItems = new ArrayList<Document>();
    for (Item item : items){
      Document line = new Document()
          .append("productId", item.getItemID())
          .append("name", item.getItemName())
          .append("quantity", item.getAmount())
          .append("lineTotal", item.getPrice()*item.getAmount());
      lineItems.add(line);
    }
    Document doc = new Document()
        .append("timestamp", date)
        .append("items", lineItems)
        .append("totalWithTax", totalWithTax);
    collection.insertOne(doc);
  }
}

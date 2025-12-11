import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class ReturnRepository {
  private final MongoCollection<Document> collection;

  public ReturnRepository(){
    collection = MongoConfig.getDatabase().getCollection("returns");
  }

  public void logReturn(Date date, List<Item> items, double total, String type){
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
        .append("total", total)
        .append("type", type);
    collection.insertOne(doc);
  }
}

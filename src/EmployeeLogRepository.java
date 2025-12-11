import java.util.Date;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class EmployeeLogRepository {
  private final MongoCollection<Document> collection;

  public EmployeeLogRepository(){
    collection = MongoConfig.getDatabase().getCollection("employee_logs");
  }

  public void log(String username, String name, String position, String action, Date timestamp){
    Document doc = new Document()
        .append("username", username)
        .append("name", name)
        .append("position", position)
        .append("action", action)
        .append("timestamp", timestamp);
    collection.insertOne(doc);
  }
}

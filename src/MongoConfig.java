import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConfig {
  private static MongoClient client;
  private static MongoDatabase database;

  private MongoConfig(){}

  public static synchronized MongoDatabase getDatabase(){
    if (database == null){
      String uri = System.getenv("MONGODB_URI");
      if (uri == null || uri.isEmpty()){
        throw new IllegalStateException("MONGODB_URI is not set. Please export it or add to your env.");
      }
      String dbName = System.getenv("MONGODB_DB");
      if (dbName == null || dbName.isEmpty()){
        dbName = "pos";
      }
      client = MongoClients.create(uri);
      database = client.getDatabase(dbName);
    }
    return database;
  }
}

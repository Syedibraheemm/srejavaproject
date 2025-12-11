import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class CouponRepository {
  private final MongoCollection<Document> collection;

  public CouponRepository(){
    collection = MongoConfig.getDatabase().getCollection("coupons");
  }

  public boolean isValid(String code){
    Document doc = collection.find(Filters.eq("code", code)).first();
    return doc != null && (!doc.containsKey("active") || doc.getBoolean("active", true));
  }
}

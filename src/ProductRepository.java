import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class ProductRepository {
  private final MongoCollection<Document> collection;

  public ProductRepository(){
    collection = MongoConfig.getDatabase().getCollection("products");
  }

  public List<Item> findByRentalFlag(boolean isRental){
    List<Item> items = new ArrayList<Item>();
    for (Document doc : collection.find(Filters.eq("isRental", isRental))){
      items.add(mapToItem(doc));
    }
    return items;
  }

  public Item findById(int id){
    Document doc = collection.find(Filters.eq("productId", id)).first();
    if (doc == null){
      return null;
    }
    return mapToItem(doc);
  }

  public void updateQuantities(List<Item> transactionItems, boolean takeFromInventory){
    for (Item item : transactionItems){
      int delta = takeFromInventory ? -item.getAmount() : item.getAmount();
      collection.updateOne(Filters.eq("productId", item.getItemID()),
          Updates.inc("quantity", delta));
    }
  }

  private Item mapToItem(Document doc){
    int productId = doc.getInteger("productId");
    String name = doc.getString("name");
    Double price = doc.getDouble("price");
    Integer qty = doc.getInteger("quantity");
    return new Item(productId, name, price.floatValue(), qty);
  }
}

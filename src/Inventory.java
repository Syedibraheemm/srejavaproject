
import java.util.*;

public class Inventory 
{
	//Singleton design pattern applied
	private static Inventory uniqueInstance = null;
        private final ProductRepository productRepository = new ProductRepository();
	//constructor
	private Inventory() {}
	
	public static synchronized Inventory getInstance()
	{
		if (uniqueInstance == null)
			uniqueInstance = new Inventory();
		return uniqueInstance;
	}
	
	//methods
	public boolean accessInventory(String databaseFile, List <Item> databaseItem)
	{
		boolean isRental = databaseFile.toLowerCase().contains("rental");
		databaseItem.clear();
		databaseItem.addAll(productRepository.findByRentalFlag(isRental));
		return true;
	}
	
	public void updateInventory(String databaseFile, List <Item> transactionItem, List <Item> databaseItem,boolean takeFromInventory)
	{
		productRepository.updateQuantities(transactionItem, takeFromInventory);
	}
	
}	


  

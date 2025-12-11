
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class POS extends PointOfSale {
  public POS(){};
  
  public void deleteTempItem(int id){
    try{
      String temp = "Database/newTemp.txt";
      if(System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
        //temp = "..\\Database\\newTemp.txt"; 
      }
      File tempF = new File(temp);
      FileReader fileR = new FileReader(tempFile);
      BufferedReader reader = new BufferedReader(fileR);
      BufferedWriter writer = new BufferedWriter(new FileWriter(tempF));
      String type= reader.readLine();
      writer.write(type);
      writer.write(System.getProperty("line.separator"));
      for (int i =0; i<transactionItem.size();i++){
        if (transactionItem.get(i).getItemID()!=id){
          writer.write(transactionItem.get(i).getItemID() +" "+ transactionItem.get(i).getAmount());
          writer.write(System.getProperty( "line.separator" ));
        }
      }
      fileR.close();
      writer.close(); 
      reader.close(); 
      File file = new File(tempFile);
      file.delete();
      tempF.renameTo(new File(tempFile));

    }
    catch(FileNotFoundException ex) {
      System.out.println(
                         "Unable to open file 'temp'"); 
    }
    catch(IOException ex) {
      System.out.println(
                         "Error reading file 'temp'");  
    }
    
  }
  
  public double endPOS(String textFile)
  {
    detectSystem();
    boolean bool=true;
    if (transactionItem.size()>0){
    totalPrice = totalPrice*tax; //calculates price with tax
    //prints total with taxes
   // bool=payment();
    
    //System.out.format("Total with taxes: %.2f\n", totalPrice);
    inventory.updateInventory(textFile, transactionItem, databaseItem,true);
    }
    //delete log file
    File file = new File(tempFile);
      file.delete();
      if(bool==true){
        writeInvoiceRecord();
      }
     databaseItem.clear();
    transactionItem.clear();
    return totalPrice;
  }
  
  public void retrieveTemp(String textFile){
    try{
      FileReader fileR = new FileReader(tempFile);
      BufferedReader textReader = new BufferedReader(fileR);
      String line=null;
     
      String[] lineSort;
      line=textReader.readLine();
      inventory.accessInventory(textFile, databaseItem);
      
      while ((line = textReader.readLine()) != null)
      {
        lineSort = line.split(" ");
        int itemNo = Integer.parseInt(lineSort[0]);
        int itemAmount = Integer.parseInt(lineSort[1]);
        enterItem(itemNo,itemAmount);
      }
      textReader.close();
      updateTotal();
    }
    catch(FileNotFoundException ex) {
      System.out.println(
                         "Unable to open file 'temp'"); 
    }
    catch(IOException ex) {
      System.out.println(
                         "Error reading file 'temp'");  
    }
    
  }
  
  private void writeInvoiceRecord(){
    SalesRepository repo = new SalesRepository();
    Calendar cal = Calendar.getInstance();
    repo.logSale(cal.getTime(), transactionItem, totalPrice);
  }
  
}

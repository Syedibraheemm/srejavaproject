
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;

public class POSSystem{
  public boolean unixOS = true; 
  public static String employeeDatabase = "Database/employeeDatabase.txt";
  public static String rentalDatabaseFile = "Database/rentalDatabase.txt"; 
  public static String itemDatabaseFile = "Database/itemDatabase.txt"; 
  public List<Employee> employees = new ArrayList<Employee>();
  private final EmployeeRepository employeeRepository = new EmployeeRepository();
  private final EmployeeLogRepository employeeLogRepository = new EmployeeLogRepository();
  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  Calendar cal=null;
  private int index=-1;
  String username="";
  String password="";
  String name="";
  
  private void readFile(){
    employees.clear();
    employees.addAll(employeeRepository.findAll());
  } 
  
  private void logInToFile(String username,String name,String position,Calendar cal){
    employeeLogRepository.log(username, name, position, "login", cal.getTime());
  }
  
  public boolean checkTemp()
  {
   String temp = "Database/temp.txt";
   File f=new File(temp);
     if(f.exists() && !f.isDirectory())
      return true;
     return false;
  }
  
  public String continueFromTemp(long phone)
  {
     String temp = "Database/temp.txt";
     File f=new File(temp);

         try{
           FileReader fileR = new FileReader(temp);
           BufferedReader textReader = new BufferedReader(fileR);
           if(f.length()==0){
             System.out.println("The log file is not valid"); 
             f.delete();
           }
           else{
             String type= textReader.readLine();
             if(type.equals("Sale")){         
               //System.out.println("Sale");
               POS sale=new POS();
               sale.retrieveTemp(itemDatabaseFile); 
               textReader.close();
               return "Sale";
             }
             else if(type.equals("Rental")){
               //System.out.println("Rental");
               POR rental=new POR(phone);
               rental.retrieveTemp(rentalDatabaseFile); 
               textReader.close();
               return "Rental";
             }
             
             else if(type.equals("Return")){
                 //System.out.println("Return");
                 POH returns = new POH(phone);
                 returns.retrieveTemp(rentalDatabaseFile);
               textReader.close();
               return "Return";
               }
             
           }
           textReader.close();
         }
         catch(FileNotFoundException ex) {
           //System.out.println(
                //              "Unable to open file 'temp'"); 
         }
         catch(IOException ex) {
           //System.out.println(
             //                 "Error reading file 'temp'");  
           //ableToOpen = false;
         }
         return "";
  }
  
  private void logOutToFile(String username,String name,String position,Calendar cal){
    employeeLogRepository.log(username, name, position, "logout", cal.getTime());
  }
  
  public void logOut(String pos){
      cal = Calendar.getInstance();
      logOutToFile(username,name,pos,cal);
  }
  
  
  public int logIn(String userAuth, String passAuth){
     readFile();
     username=userAuth;
     boolean find=false;
     for(int i=0;i<employees.size();i++){
       if(username.equals((employees.get(i)).getUsername())){
          find=true;
          index=i;
          break;
         }
       }
     if (find == true)
     {
      password=passAuth;
      if(!password.equals((employees.get(index)).getPassword())){
        return 0; //didnt find employee password
      }
      else{
          //employee logIn file update
           cal = Calendar.getInstance();
           name=(employees.get(index)).getName();
          logInToFile((employees.get(index)).getUsername(),name,(employees.get(index)).getPosition(),cal);
          
      if(((employees.get(index)).getPosition()).equals("Cashier")){
        return 1;  //returns cashier status
      }
      else if(((employees.get(index)).getPosition()).equals("Admin")){
        return 2; //returns admin status
      }  
     }
     }
     return 0;//didnt find employee username
     
  }
  
  
  
  
}

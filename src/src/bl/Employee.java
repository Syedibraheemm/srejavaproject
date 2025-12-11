package bl;

public class Employee {
 
 //attributes
 private String username;
 private String name;
 private String position;
 private String password; //amount of items on inventory
 
 //methods
 public Employee(String username,String name, String position,String password)
 {
  this.username = username; this.name = name; this.position = position; this.password = password;
 }
 
 public String getUsername() {return username;}
 public String getName() {return name;}
 public String getPosition() {return position;}
 public String getPassword() {return password;}      
 //void setUsername(String usename){this.username = username;};
 public void setName(String name){this.name = name;};
 public void setPosition(String position){this.position = position;};
 public void setPassword(String password){this.password = password;};
}

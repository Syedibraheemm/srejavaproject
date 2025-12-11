
import java.util.*;
import java.io.*;
public class EmployeeManagement{
  String temp ="Database/newEmployeeDatabase.txt";
  public boolean unixOS = true; 
  public static String employeeDatabase = "Database/employeeDatabase.txt";
  public List<Employee> employees = new ArrayList<Employee>();
  private final EmployeeRepository repository = new EmployeeRepository();
  public EmployeeManagement(){}
  
  public List<Employee> getEmployeeList()
  {
	  readFile();
	  return employees;
  }
  
  
  
  public void add(String name,String password, boolean employee)
  {
	  repository.add(name, password, employee);
  }
  
  public boolean delete(String username)
  {
	  return repository.delete(username);
  }
  
 
  
  public int update(String username, String password, String position, String name)
  {
	  return repository.update(username, password, position, name);
  }
  
  
  
  
  
  
  private void readFile(){
    employees.clear();
    employees.addAll(repository.findAll());
  }
}

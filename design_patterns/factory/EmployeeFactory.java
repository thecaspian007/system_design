public class EmployeeFactory {
   public static Employee getEmployee(String empType) {
       if (empType.trim().equalsIgnoreCase("SOFTWARE")) {
           return new Software();
       }
       else if (empType.trim().equalsIgnoreCase("OPERATIONS")) {
        return new Operations();
       }
       else {
           return null;
       }
   }
}
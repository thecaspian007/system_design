public class EmployeeFactory {
   public static Employee getEmployee(AbstractEmployeeFactory factory) {
      return factory.createEmployee();
   }
}
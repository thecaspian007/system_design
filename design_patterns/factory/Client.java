public class Client {
    public static void main(String[] args) {
        Employee employee1 = EmployeeFactory.getEmployee("SOFTWARE");
        int salary1 = employee1.salary();
        System.out.println("Salary1 : " + salary1);

        Employee employee2 = EmployeeFactory.getEmployee("OPERATIONS");
        int salary2 = employee2.salary();
        System.out.println("Salary2 : " + salary2);
    }
}
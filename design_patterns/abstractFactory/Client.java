public class Client {
    public static void main(String[] args) {
        Employee employee1 = EmployeeFactory.getEmployee(new SoftwareFactory());
        int salary1 = employee1.salary();
        String name1 = employee1.name();
        System.out.println("Salary1 : " + salary1);
        System.out.println("name1 : " + name1);

        Employee employee2 = EmployeeFactory.getEmployee(new OperationsFactory());
        int salary2 = employee2.salary();
        String name2 = employee2.name();
        System.out.println("Salary2 : " + salary2);
        System.out.println("name2 : " + name2);

    }
}
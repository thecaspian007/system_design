public class OperationsFactory extends AbstractEmployeeFactory {
    
    @Override
    public Employee createEmployee() {
        return new Operations();
    }
}
public class SoftwareFactory extends AbstractEmployeeFactory {
    
    @Override
    public Employee createEmployee() {
        return new Software();
    }
}
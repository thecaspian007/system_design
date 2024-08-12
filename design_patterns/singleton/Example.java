public class Example {
    public static void main(String[] args) {
        Sport sport1 = Sport.getSport();
        System.out.println(sport1.hashCode());

        Sport sport2 = Sport.getSport();
        System.out.println(sport2.hashCode());

    }
}
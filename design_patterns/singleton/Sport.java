public class Sport {
    private static Sport sport;
    private Sport() {

    }

    public static Sport getSport() {
        if (sport == null)
        {
            synchronized (Sport.class)
            {
                if (sport == null) {
                    sport = new Sport();
                }
            }
        }
        return sport; 
    }
}
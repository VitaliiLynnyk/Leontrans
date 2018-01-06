package leontrans.leontranstm.utils;

public class RoutPointsCoordinates {
    private String fromLat;
    private String fromLng;

    private String toLat;
    private String toLng;

    public RoutPointsCoordinates(String fromLat, String fromLng, String toLat, String toLng) {
        this.fromLat = fromLat;
        this.fromLng = fromLng;
        this.toLat = toLat;
        this.toLng = toLng;
    }

    public String getFromLat() {
        return fromLat;
    }

    public String getFromLng() {
        return fromLng;
    }

    public String getToLat() {
        return toLat;
    }

    public String getToLng() {
        return toLng;
    }
}

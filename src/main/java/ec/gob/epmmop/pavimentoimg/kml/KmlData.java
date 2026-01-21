package ec.gob.epmmop.pavimentoimg.kml;

import java.util.ArrayList;
import java.util.List;

public class KmlData {

    public static class Placemark {
        private final String name;
        private final double lon;
        private final double lat;

        public Placemark(String name, double lon, double lat) {
            this.name = name;
            this.lon = lon;
            this.lat = lat;
        }

        public String getName() { return name; }
        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    private final List<Placemark> placemarks = new ArrayList<>();

    public void add(Placemark p) {
        placemarks.add(p);
    }

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }
}

package seatsio.charts;

import seatsio.util.ValueObject;

public class Zone extends ValueObject {

    public final String key;
    public final String label;

    public Zone(String key, String label) {
        this.key = key;
        this.label = label;
    }

}

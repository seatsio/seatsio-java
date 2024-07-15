package seatsio.charts;

import seatsio.util.ValueObject;

public class Zone extends ValueObject {

    private final String key;
    private final String label;

    public Zone(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }
}

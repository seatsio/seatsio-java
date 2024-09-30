package seatsio.charts;

import seatsio.util.ValueObject;

public class FloorInfo extends ValueObject {

    private final String name;
    private final String displayName;

    public FloorInfo(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}

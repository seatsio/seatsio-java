package seatsio.events;

import seatsio.util.ValueObject;

public class Entrance extends ValueObject {

    public final String label;

    protected Entrance() {
        this(null);
    }

    public Entrance(String label) {
        this.label = label;
    }

}

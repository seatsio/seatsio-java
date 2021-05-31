package seatsio.events;

import seatsio.util.ValueObject;

public class Entrance extends ValueObject {

    private String label;

    protected Entrance() {
    }

    public Entrance(String label) {
        this.label = label;
    }

}

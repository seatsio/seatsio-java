package seatsio.events;

import seatsio.util.ValueObject;

public class LabelAndType extends ValueObject {

    public String label;
    public String type;

    protected LabelAndType() {
    }

    public LabelAndType(String label, String type) {
        this.label = label;
        this.type = type;
    }

}

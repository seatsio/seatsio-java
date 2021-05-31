package seatsio.events;

import seatsio.util.ValueObject;

public class Labels extends ValueObject {

    public LabelAndType own;
    public LabelAndType parent;
    public String section;

    protected Labels() {
    }

    public Labels(String ownLabel, String ownType) {
        this.own = new LabelAndType(ownLabel, ownType);
    }

    public Labels(String ownLabel, String ownType, String parentLabel, String parentType) {
        this(ownLabel, ownType);
        this.parent = new LabelAndType(parentLabel, parentType);
    }

    public Labels(String ownLabel, String ownType, String parentLabel, String parentType, String section) {
        this(ownLabel, ownType, parentLabel, parentType);
        this.section = section;
    }

}

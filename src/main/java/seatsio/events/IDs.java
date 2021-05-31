package seatsio.events;

import seatsio.util.ValueObject;

public class IDs extends ValueObject {

    public String own;
    public String parent;
    public String section;

    protected IDs() {
    }

    public IDs(String own, String parent, String section) {
        this.own = own;
        this.parent = parent;
        this.section = section;
    }

}

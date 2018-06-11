package seatsio.events;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class Labels {

    public LabelAndType own;
    public LabelAndType parent;
    public String section;

    protected Labels() {
    }

    public Labels(String ownLabel, String ownType, String section) {
        this.own = new LabelAndType(ownLabel, ownType);
        this.section = section;
    }

    public Labels(String ownLabel, String ownType, String parentLabel, String parentType, String section) {
        this(ownLabel, ownType, section);
        this.parent = new LabelAndType(parentLabel, parentType);
    }

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}

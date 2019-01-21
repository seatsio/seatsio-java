package seatsio.events;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class Labels {

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

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}

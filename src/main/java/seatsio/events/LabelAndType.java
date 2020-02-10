package seatsio.events;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class LabelAndType {

    public String label;
    public String type;

    protected LabelAndType() {
    }

    public LabelAndType(String label, String type) {
        this.label = label;
        this.type = type;
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

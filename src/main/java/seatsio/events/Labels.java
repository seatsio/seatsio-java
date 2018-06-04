package seatsio.events;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class Labels {

    public String own;
    public String row;
    public String table;
    public String section;

    public Labels(String own, String row, String table, String section) {
        this.own = own;
        this.row = row;
        this.table = table;
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

package seatsio.reports.usage.detailsForMonth;

import seatsio.util.ValueObject;

public class UsageEvent extends ValueObject {

    public final long id;
    public final String key;
    public final boolean deleted;

    public UsageEvent(long id, String key, boolean deleted) {
        this.id = id;
        this.key = key;
        this.deleted = deleted;
    }
}

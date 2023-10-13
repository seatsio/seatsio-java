package seatsio.reports.usage.detailsForMonth;

import seatsio.util.ValueObject;

public class UsageByEvent extends ValueObject {

    public final UsageEvent event;
    public final int numUsedObjects;

    public UsageByEvent(UsageEvent event, int numUsedObjects) {
        this.event = event;
        this.numUsedObjects = numUsedObjects;
    }
}

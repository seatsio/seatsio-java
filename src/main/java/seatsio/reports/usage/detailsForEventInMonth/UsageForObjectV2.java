package seatsio.reports.usage.detailsForEventInMonth;

import seatsio.reports.usage.UsageReason;
import seatsio.util.ValueObject;

import java.util.Map;

public class UsageForObjectV2 extends ValueObject {

    public final String object;
    public final int numUsedObjects;
    public final Map<UsageReason, Integer> usageByReason;

    public UsageForObjectV2(String object, int numUsedObjects, Map<UsageReason, Integer> usageByReason) {
        this.object = object;
        this.numUsedObjects = numUsedObjects;
        this.usageByReason = usageByReason;
    }
}

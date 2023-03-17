package seatsio.reports.usage.detailsForEventInMonth;

import seatsio.reports.usage.UsageReason;

import java.util.Map;

public class UsageForObjectV2 {

    public String object;
    public int numUsedObjects;
    public Map<UsageReason, Integer> usageByReason;
}

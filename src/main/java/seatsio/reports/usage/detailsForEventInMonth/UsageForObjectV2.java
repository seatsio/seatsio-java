package seatsio.reports.usage.detailsForEventInMonth;

import seatsio.reports.usage.UsageReason;

import java.util.Map;

public record UsageForObjectV2(String object, int numUsedObjects, Map<UsageReason, Integer> usageByReason) {

}

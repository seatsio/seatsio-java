package seatsio.reports.usage.summaryForMonths;

import seatsio.reports.usage.Month;
import seatsio.util.ValueObject;

public class UsageSummaryForMonth extends ValueObject {

    public final Month month;
    public final int numUsedObjects;

    public UsageSummaryForMonth(Month month, int numUsedObjects) {
        this.month = month;
        this.numUsedObjects = numUsedObjects;
    }
}

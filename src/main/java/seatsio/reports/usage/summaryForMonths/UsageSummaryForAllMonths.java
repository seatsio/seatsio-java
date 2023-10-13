package seatsio.reports.usage.summaryForMonths;

import seatsio.util.ValueObject;

import java.time.Instant;
import java.util.List;

public class UsageSummaryForAllMonths extends ValueObject {

    public final Instant usageCutoffDate;
    public final List<UsageSummaryForMonth> usage;

    public UsageSummaryForAllMonths(Instant usageCutoffDate, List<UsageSummaryForMonth> usage) {
        this.usageCutoffDate = usageCutoffDate;
        this.usage = usage;
    }
}

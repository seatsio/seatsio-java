package seatsio.reports.usage.summaryForMonths;

import java.time.Instant;
import java.util.List;

public record UsageSummaryForAllMonths(Instant usageCutoffDate, List<UsageSummaryForMonth> usage) {

}

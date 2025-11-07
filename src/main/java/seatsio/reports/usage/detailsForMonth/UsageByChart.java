package seatsio.reports.usage.detailsForMonth;

import java.util.List;

public record UsageByChart(UsageChart chart, List<UsageByEvent> usageByEvent) {

}

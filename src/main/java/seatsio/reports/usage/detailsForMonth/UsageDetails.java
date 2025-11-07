package seatsio.reports.usage.detailsForMonth;

import java.util.List;

public record UsageDetails(Long workspace, List<UsageByChart> usageByChart) {

}

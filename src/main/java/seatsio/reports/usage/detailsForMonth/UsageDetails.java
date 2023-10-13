package seatsio.reports.usage.detailsForMonth;

import seatsio.util.ValueObject;

import java.util.List;

public class UsageDetails extends ValueObject {

    public final Long workspace;
    public final List<UsageByChart> usageByChart;

    public UsageDetails(Long workspace, List<UsageByChart> usageByChart) {
        this.workspace = workspace;
        this.usageByChart = usageByChart;
    }
}

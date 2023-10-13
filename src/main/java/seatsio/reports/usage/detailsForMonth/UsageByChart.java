package seatsio.reports.usage.detailsForMonth;

import seatsio.util.ValueObject;

import java.util.List;

public class UsageByChart extends ValueObject {

    public final UsageChart chart;
    public final List<UsageByEvent> usageByEvent;

    public UsageByChart(UsageChart chart, List<UsageByEvent> usageByEvent) {
        this.chart = chart;
        this.usageByEvent = usageByEvent;
    }
}

package seatsio.reports.usage.detailsForMonth;

import seatsio.util.ValueObject;

public class UsageChart extends ValueObject {

    public final String name;
    public final String key;

    public UsageChart(String name, String key) {
        this.name = name;
        this.key = key;
    }
}

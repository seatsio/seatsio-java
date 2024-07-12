package seatsio.reports.charts;

import seatsio.util.ValueObject;

import java.util.Map;

public class ChartReportSummaryItem extends ValueObject {

    public int count;
    public Map<String, Integer> byCategoryKey;
    public Map<String, Integer> byCategoryLabel;
    public Map<String, Integer> bySection;
    public Map<String, Integer> byObjectType;
    public Map<String, Integer> byZone;
}

package seatsio.reports.events;

import seatsio.util.ValueObject;

import java.util.Map;

public class EventReportSummaryItem extends ValueObject {

    public int count;
    public Map<String, Integer> byStatus;
    public Map<String, Integer> byCategoryKey;
    public Map<String, Integer> byCategoryLabel;
    public Map<String, Integer> bySection;
    public Map<String, Integer> bySelectability;
    public Map<String, Integer> byChannel;
}

package seatsio.reports.events;

import seatsio.util.ValueObject;

import java.util.Map;

public class EventReportDeepSummaryItem extends ValueObject {

    public int count;
    public Map<String, EventReportSummaryItem> byStatus;
    public Map<String, EventReportSummaryItem> byCategoryKey;
    public Map<String, EventReportSummaryItem> byCategoryLabel;
    public Map<String, EventReportSummaryItem> bySection;
    public Map<String, EventReportSummaryItem> byAvailability;
    public Map<String, EventReportSummaryItem> byChannel;
    public Map<String, EventReportSummaryItem> byZone;
}

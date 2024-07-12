package seatsio.reports.events;

import seatsio.util.ValueObject;

import java.util.Map;

public class EventReportSummaryItem extends ValueObject {

    public int count;
    public Map<String, Integer> byStatus;
    public Map<String, Integer> byCategoryKey;
    public Map<String, Integer> byCategoryLabel;
    public Map<String, Integer> bySection;
    public Map<String, Integer> byAvailability;
    public Map<String, Integer> byAvailabilityReason;
    public Map<String, Integer> byChannel;
    public Map<String, Integer> byObjectType;
    public Map<String, Integer> byZone;
}

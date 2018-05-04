package seatsio.eventreports;

import java.util.Map;

public class EventReportSummaryItemBuilder {

    private int count;
    private Map<String, Integer> bySection;
    private Map<String, Integer> byCategoryKey;
    private Map<String, Integer> byCategoryLabel;
    private Map<String, Integer> byStatus;

    public static EventReportSummaryItemBuilder anEventReportSummaryItem() {
        return new EventReportSummaryItemBuilder();
    }

    public EventReportSummaryItemBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public EventReportSummaryItemBuilder withBySection(Map<String, Integer> bySection) {
        this.bySection = bySection;
        return this;
    }

    public EventReportSummaryItemBuilder withByCategoryKey(Map<String, Integer> byCategoryKey) {
        this.byCategoryKey = byCategoryKey;
        return this;
    }

    public EventReportSummaryItemBuilder withByCategoryLabel(Map<String, Integer> byCategoryLabel) {
        this.byCategoryLabel = byCategoryLabel;
        return this;
    }

    public EventReportSummaryItemBuilder withByStatus(Map<String, Integer> byStatus) {
        this.byStatus = byStatus;
        return this;
    }

    public EventReportSummaryItem build() {
        EventReportSummaryItem eventSummaryReportItem = new EventReportSummaryItem();
        eventSummaryReportItem.count = count;
        eventSummaryReportItem.byStatus = byStatus;
        eventSummaryReportItem.byCategoryKey = byCategoryKey;
        eventSummaryReportItem.byCategoryLabel = byCategoryLabel;
        eventSummaryReportItem.bySection = bySection;
        return eventSummaryReportItem;
    }
}

package seatsio.events;

import java.util.Map;

public class EventSummaryReportItemBuilder {

    private int count;
    private Map<String, Integer> bySection;
    private Map<String, Integer> byCategoryKey;
    private Map<String, Integer> byCategoryLabel;
    private Map<String, Integer> byStatus;

    public static EventSummaryReportItemBuilder anEventSummaryReportItem() {
        return new EventSummaryReportItemBuilder();
    }

    public EventSummaryReportItemBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public EventSummaryReportItemBuilder withBySection(Map<String, Integer> bySection) {
        this.bySection = bySection;
        return this;
    }

    public EventSummaryReportItemBuilder withByCategoryKey(Map<String, Integer> byCategoryKey) {
        this.byCategoryKey = byCategoryKey;
        return this;
    }

    public EventSummaryReportItemBuilder withByCategoryLabel(Map<String, Integer> byCategoryLabel) {
        this.byCategoryLabel = byCategoryLabel;
        return this;
    }

    public EventSummaryReportItemBuilder withByStatus(Map<String, Integer> byStatus) {
        this.byStatus = byStatus;
        return this;
    }

    public EventSummaryReportItem build() {
        EventSummaryReportItem eventSummaryReportItem = new EventSummaryReportItem();
        eventSummaryReportItem.count = count;
        eventSummaryReportItem.byStatus = byStatus;
        eventSummaryReportItem.byCategoryKey = byCategoryKey;
        eventSummaryReportItem.byCategoryLabel = byCategoryLabel;
        eventSummaryReportItem.bySection = bySection;
        return eventSummaryReportItem;
    }
}

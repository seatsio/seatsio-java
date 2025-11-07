package seatsio.reports.events;

import java.util.Map;

public class EventReportDeepSummaryItemBuilder {

    private int count;
    private Map<String, EventReportSummaryItem> bySection;
    private Map<String, EventReportSummaryItem> byCategoryKey;
    private Map<String, EventReportSummaryItem> byCategoryLabel;
    private Map<String, EventReportSummaryItem> byStatus;
    private Map<String, EventReportSummaryItem> byAvailability;
    private Map<String, EventReportSummaryItem> byChannel;
    private Map<String, EventReportSummaryItem> byZone;

    public static EventReportDeepSummaryItemBuilder anEventReportDeepSummaryItem() {
        return new EventReportDeepSummaryItemBuilder();
    }

    public EventReportDeepSummaryItemBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withBySection(Map<String, EventReportSummaryItem> bySection) {
        this.bySection = bySection;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withByCategoryKey(Map<String, EventReportSummaryItem> byCategoryKey) {
        this.byCategoryKey = byCategoryKey;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withByCategoryLabel(Map<String, EventReportSummaryItem> byCategoryLabel) {
        this.byCategoryLabel = byCategoryLabel;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withByStatus(Map<String, EventReportSummaryItem> byStatus) {
        this.byStatus = byStatus;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withByAvailability(Map<String, EventReportSummaryItem> byAvailability) {
        this.byAvailability = byAvailability;
        return this;
    }

    public EventReportDeepSummaryItemBuilder withByChannel(Map<String, EventReportSummaryItem> byChannel) {
        this.byChannel = byChannel;
        return this;
    }

    public EventReportDeepSummaryItem build() {
        return new EventReportDeepSummaryItem(count, byStatus, byCategoryKey, byCategoryLabel, bySection, byAvailability, byChannel, byZone);
    }
}

package seatsio.reports.events;

import java.util.Map;

public class EventReportSummaryItemBuilder {

    private int count;
    private Map<String, Integer> bySection;
    private Map<String, Integer> byCategoryKey;
    private Map<String, Integer> byCategoryLabel;
    private Map<String, Integer> byStatus;
    private Map<String, Integer> byAvailability;
    private Map<String, Integer> byAvailabilityReason;
    private Map<String, Integer> byChannel;
    private Map<String, Integer> byObjectType;
    private Map<String, Integer> byZone;

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

    public EventReportSummaryItemBuilder withByAvailability(Map<String, Integer> byAvailability) {
        this.byAvailability = byAvailability;
        return this;
    }

    public EventReportSummaryItemBuilder withByAvailabilityReason(Map<String, Integer> byAvailabilityReason) {
        this.byAvailabilityReason = byAvailabilityReason;
        return this;
    }

    public EventReportSummaryItemBuilder withByChannel(Map<String, Integer> byChannel) {
        this.byChannel = byChannel;
        return this;
    }

    public EventReportSummaryItemBuilder withByObjectType(Map<String, Integer> byObjectType) {
        this.byObjectType = byObjectType;
        return this;
    }

    public EventReportSummaryItemBuilder withByZone(Map<String, Integer> byZone) {
        this.byZone = byZone;
        return this;
    }

    public EventReportSummaryItem build() {
        return new EventReportSummaryItem(count, byStatus, byCategoryKey, byCategoryLabel, bySection, byAvailability,
                byAvailabilityReason, byChannel, byObjectType, byZone);
    }
}

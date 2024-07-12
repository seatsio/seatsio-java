package seatsio.reports.charts;

import java.util.Map;

public class ChartReportSummaryItemBuilder {

    private int count;
    private Map<String, Integer> bySection;
    private Map<String, Integer> byCategoryKey;
    private Map<String, Integer> byCategoryLabel;
    private Map<String, Integer> byObjectType;
    private Map<String, Integer> byZone;

    public static ChartReportSummaryItemBuilder aChartReportSummaryItem() {
        return new ChartReportSummaryItemBuilder();
    }

    public ChartReportSummaryItemBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public ChartReportSummaryItemBuilder withBySection(Map<String, Integer> bySection) {
        this.bySection = bySection;
        return this;
    }

    public ChartReportSummaryItemBuilder withByCategoryKey(Map<String, Integer> byCategoryKey) {
        this.byCategoryKey = byCategoryKey;
        return this;
    }

    public ChartReportSummaryItemBuilder withByCategoryLabel(Map<String, Integer> byCategoryLabel) {
        this.byCategoryLabel = byCategoryLabel;
        return this;
    }

    public ChartReportSummaryItemBuilder withByObjectType(Map<String, Integer> byObjectType) {
        this.byObjectType = byObjectType;
        return this;
    }

    public ChartReportSummaryItemBuilder withByZone(Map<String, Integer> byZone) {
        this.byZone = byZone;
        return this;
    }

    public ChartReportSummaryItem build() {
        ChartReportSummaryItem chartSummaryReportItem = new ChartReportSummaryItem();
        chartSummaryReportItem.count = count;
        chartSummaryReportItem.byCategoryKey = byCategoryKey;
        chartSummaryReportItem.byCategoryLabel = byCategoryLabel;
        chartSummaryReportItem.bySection = bySection;
        chartSummaryReportItem.byObjectType = byObjectType;
        chartSummaryReportItem.byZone = byZone;
        return chartSummaryReportItem;
    }
}

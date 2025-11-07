package seatsio.reports.charts;

import java.util.Map;

public record ChartReportSummaryItem(int count, Map<String, Integer> byCategoryKey,
                                     Map<String, Integer> byCategoryLabel, Map<String, Integer> bySection,
                                     Map<String, Integer> byObjectType, Map<String, Integer> byZone) {

}

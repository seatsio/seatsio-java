package seatsio.reports.events;

import java.util.Map;

public record EventReportDeepSummaryItem(int count, Map<String, EventReportSummaryItem> byStatus,
                                         Map<String, EventReportSummaryItem> byCategoryKey,
                                         Map<String, EventReportSummaryItem> byCategoryLabel,
                                         Map<String, EventReportSummaryItem> bySection,
                                         Map<String, EventReportSummaryItem> byAvailability,
                                         Map<String, EventReportSummaryItem> byChannel,
                                         Map<String, EventReportSummaryItem> byZone) {
}

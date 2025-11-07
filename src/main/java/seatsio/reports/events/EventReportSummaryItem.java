package seatsio.reports.events;

import java.util.Map;

public record EventReportSummaryItem(int count, Map<String, Integer> byStatus, Map<String, Integer> byCategoryKey,
                                     Map<String, Integer> byCategoryLabel, Map<String, Integer> bySection,
                                     Map<String, Integer> byAvailability, Map<String, Integer> byAvailabilityReason,
                                     Map<String, Integer> byChannel, Map<String, Integer> byObjectType,
                                     Map<String, Integer> byZone) {

}

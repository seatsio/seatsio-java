package seatsio.reports.events;

import com.google.gson.reflect.TypeToken;
import seatsio.events.EventObjectInfo;
import seatsio.reports.Reports;
import seatsio.util.UnirestWrapper;

import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.get;

public class EventReports extends Reports {

    private final UnirestWrapper unirest;

    public EventReports(String baseUrl, UnirestWrapper unirest) {
        super(baseUrl, "events", unirest);
        this.unirest = unirest;
    }

    public Map<String, List<EventObjectInfo>> byLabel(String eventKey) {
        return fetchReport("byLabel", eventKey);
    }

    public List<EventObjectInfo> byLabel(String eventKey, String label) {
        return fetchReportFiltered("byLabel", eventKey, label);
    }

    public Map<String, List<EventObjectInfo>> byStatus(String eventKey) {
        return fetchReport("byStatus", eventKey);
    }

    public List<EventObjectInfo> byStatus(String eventKey, String status) {
        return fetchReportFiltered("byStatus", eventKey, status);
    }

    public Map<String, EventReportSummaryItem> summaryByStatus(String eventKey) {
        return fetchSummaryReport("byStatus", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByStatus(String eventKey) {
        return fetchDeepSummaryReport("byStatus", eventKey);
    }

    public Map<String, List<EventObjectInfo>> byObjectType(String eventKey) {
        return fetchReport("byObjectType", eventKey);
    }

    public List<EventObjectInfo> byObjectType(String eventKey, String status) {
        return fetchReportFiltered("byObjectType", eventKey, status);
    }

    public Map<String, EventReportSummaryItem> summaryByObjectType(String eventKey) {
        return fetchSummaryReport("byObjectType", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByObjectType(String eventKey) {
        return fetchDeepSummaryReport("byObjectType", eventKey);
    }

    public Map<String, List<EventObjectInfo>> byCategoryLabel(String eventKey) {
        return fetchReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryLabel(String eventKey) {
        return fetchDeepSummaryReport("byCategoryLabel", eventKey);
    }

    public List<EventObjectInfo> byCategoryLabel(String eventKey, String categoryLabel) {
        return fetchReportFiltered("byCategoryLabel", eventKey, categoryLabel);
    }

    public Map<String, List<EventObjectInfo>> byCategoryKey(String eventKey) {
        return fetchReport("byCategoryKey", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return fetchSummaryReport("byCategoryKey", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryKey(String eventKey) {
        return fetchDeepSummaryReport("byCategoryKey", eventKey);
    }

    public List<EventObjectInfo> byCategoryKey(String eventKey, String categoryKey) {
        return fetchReportFiltered("byCategoryKey", eventKey, categoryKey);
    }

    public Map<String, List<EventObjectInfo>> byOrderId(String eventKey) {
        return fetchReport("byOrderId", eventKey);
    }

    public List<EventObjectInfo> byOrderId(String eventKey, String orderId) {
        return fetchReportFiltered("byOrderId", eventKey, orderId);
    }

    public Map<String, List<EventObjectInfo>> bySection(String eventKey) {
        return fetchReport("bySection", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryBySection(String eventKey) {
        return fetchDeepSummaryReport("bySection", eventKey);
    }

    public List<EventObjectInfo> bySection(String eventKey, String section) {
        return fetchReportFiltered("bySection", eventKey, section);
    }

    public Map<String, List<EventObjectInfo>> byChannel(String eventKey) {
        return fetchReport("byChannel", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByChannel(String channelKey) {
        return fetchSummaryReport("byChannel", channelKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByChannel(String channelKey) {
        return fetchDeepSummaryReport("byChannel", channelKey);
    }

    public List<EventObjectInfo> byChannel(String eventKey, String channelKey) {
        return fetchReportFiltered("byChannel", eventKey, channelKey);
    }

    public Map<String, List<EventObjectInfo>> byAvailability(String eventKey) {
        return fetchReport("byAvailability", eventKey);
    }

    public List<EventObjectInfo> byAvailability(String eventKey, String availability) {
        return fetchReportFiltered("byAvailability", eventKey, availability);
    }

    public Map<String, List<EventObjectInfo>> byAvailabilityReason(String eventKey) {
        return fetchReport("byAvailabilityReason", eventKey);
    }

    public List<EventObjectInfo> byAvailabilityReason(String eventKey, String availabilityReason) {
        return fetchReportFiltered("byAvailabilityReason", eventKey, availabilityReason);
    }

    public Map<String, EventReportSummaryItem> summaryByAvailability(String eventKey) {
        return fetchSummaryReport("byAvailability", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByAvailabilityReason(String eventKey) {
        return fetchSummaryReport("byAvailabilityReason", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByAvailability(String eventKey) {
        return fetchDeepSummaryReport("byAvailability", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByAvailabilityReason(String eventKey) {
        return fetchDeepSummaryReport("byAvailabilityReason", eventKey);
    }

    @Override
    protected TypeToken<Map<String, List<EventObjectInfo>>> getTypeToken() {
        return new TypeToken<Map<String, List<EventObjectInfo>>>() {
        };
    }

    @Override
    protected TypeToken<Map<String, EventReportSummaryItem>> getSummaryTypeToken() {
        return new TypeToken<Map<String, EventReportSummaryItem>>() {
        };
    }

    private Map<String, EventReportDeepSummaryItem> fetchDeepSummaryReport(String reportType, String eventKey) {
        String result = fetchRawDeepSummaryReport(reportType, eventKey);
        TypeToken<Map<String, EventReportDeepSummaryItem>> typeToken = new TypeToken<Map<String, EventReportDeepSummaryItem>>() {
        };
        return gson().fromJson(result, typeToken.getType());
    }

    private String fetchRawDeepSummaryReport(String reportType, String eventKey) {
        return unirest.stringResponse(get(baseUrl + "/reports/events/{key}/{reportType}/summary/deep")
                .routeParam("key", eventKey)
                .routeParam("reportType", reportType));
    }

}

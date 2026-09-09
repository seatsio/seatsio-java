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
    private final boolean seasonBookingsPropagated;

    public EventReports(String baseUrl, UnirestWrapper unirest) {
        this(baseUrl, unirest, true);
    }

    private EventReports(String baseUrl, UnirestWrapper unirest, boolean seasonBookingsPropagated) {
        super(baseUrl, "events", unirest);
        this.unirest = unirest;
        this.seasonBookingsPropagated = seasonBookingsPropagated;
    }

    public EventReports withSeasonBookingsNotPropagated() {
        return new EventReports(this.baseUrl, this.unirest, false);
    }

    public List<EventObjectInfo> flatList(String eventKey) {
        String result = unirest.stringResponse(get(baseUrl + "/reports/events/{key}")
                .routeParam("key", eventKey)
                .queryString(toQueryParams()));
        TypeToken<List<EventObjectInfo>> typeToken = new TypeToken<List<EventObjectInfo>>() {};
        return gson().fromJson(result, typeToken.getType());
    }

    public String flatListCsv(String eventKey) {
        return unirest.stringResponse(get(baseUrl + "/reports/events/{key}.csv")
                .routeParam("key", eventKey)
                .queryString(toQueryParams()));
    }

    public Map<String, List<EventObjectInfo>> byLabel(String eventKey) {
        return fetchReport("byLabel", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byLabel(String eventKey, String label) {
        return fetchReportFiltered("byLabel", eventKey, label, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byStatus(String eventKey) {
        return fetchReport("byStatus", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byStatus(String eventKey, String status) {
        return fetchReportFiltered("byStatus", eventKey, status, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByStatus(String eventKey) {
        return fetchSummaryReport("byStatus", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByStatus(String eventKey) {
        return fetchDeepSummaryReport("byStatus", eventKey);
    }

    public Map<String, List<EventObjectInfo>> byObjectType(String eventKey) {
        return fetchReport("byObjectType", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byObjectType(String eventKey, String status) {
        return fetchReportFiltered("byObjectType", eventKey, status, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByObjectType(String eventKey) {
        return fetchSummaryReport("byObjectType", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByObjectType(String eventKey) {
        return fetchDeepSummaryReport("byObjectType", eventKey);
    }

    public Map<String, List<EventObjectInfo>> byCategoryLabel(String eventKey) {
        return fetchReport("byCategoryLabel", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryLabel(String eventKey) {
        return fetchDeepSummaryReport("byCategoryLabel", eventKey);
    }

    public List<EventObjectInfo> byCategoryLabel(String eventKey, String categoryLabel) {
        return fetchReportFiltered("byCategoryLabel", eventKey, categoryLabel, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byCategoryKey(String eventKey) {
        return fetchReport("byCategoryKey", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return fetchSummaryReport("byCategoryKey", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryKey(String eventKey) {
        return fetchDeepSummaryReport("byCategoryKey", eventKey);
    }

    public List<EventObjectInfo> byCategoryKey(String eventKey, String categoryKey) {
        return fetchReportFiltered("byCategoryKey", eventKey, categoryKey, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byOrderId(String eventKey) {
        return fetchReport("byOrderId", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byOrderId(String eventKey, String orderId) {
        return fetchReportFiltered("byOrderId", eventKey, orderId, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> bySection(String eventKey) {
        return fetchReport("bySection", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryBySection(String eventKey) {
        return fetchDeepSummaryReport("bySection", eventKey);
    }

    public List<EventObjectInfo> bySection(String eventKey, String section) {
        return fetchReportFiltered("bySection", eventKey, section, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byZone(String eventKey) {
        return fetchReport("byZone", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByZone(String eventKey) {
        return fetchSummaryReport("byZone", eventKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByZone(String eventKey) {
        return fetchDeepSummaryReport("byZone", eventKey);
    }

    public List<EventObjectInfo> byZone(String eventKey, String zone) {
        return fetchReportFiltered("byZone", eventKey, zone, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byChannel(String eventKey) {
        return fetchReport("byChannel", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByChannel(String channelKey) {
        return fetchSummaryReport("byChannel", channelKey, toQueryParams());
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByChannel(String channelKey) {
        return fetchDeepSummaryReport("byChannel", channelKey);
    }

    public List<EventObjectInfo> byChannel(String eventKey, String channelKey) {
        return fetchReportFiltered("byChannel", eventKey, channelKey, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byAvailability(String eventKey) {
        return fetchReport("byAvailability", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byAvailability(String eventKey, String availability) {
        return fetchReportFiltered("byAvailability", eventKey, availability, toQueryParams());
    }

    public Map<String, List<EventObjectInfo>> byAvailabilityReason(String eventKey) {
        return fetchReport("byAvailabilityReason", eventKey, toQueryParams());
    }

    public List<EventObjectInfo> byAvailabilityReason(String eventKey, String availabilityReason) {
        return fetchReportFiltered("byAvailabilityReason", eventKey, availabilityReason, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByAvailability(String eventKey) {
        return fetchSummaryReport("byAvailability", eventKey, toQueryParams());
    }

    public Map<String, EventReportSummaryItem> summaryByAvailabilityReason(String eventKey) {
        return fetchSummaryReport("byAvailabilityReason", eventKey, toQueryParams());
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
                .routeParam("reportType", reportType)
                .queryString(toQueryParams()));
    }

    private Map<String, Object> toQueryParams() {
        if (seasonBookingsPropagated) {
            return null;
        }
        return Map.of("seasonBookingsPropagated", false);
    }

}

package seatsio.reports.events;

import com.google.gson.reflect.TypeToken;
import seatsio.events.ObjectInfo;
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

    public Map<String, List<ObjectInfo>> byLabel(String eventKey) {
        return fetchReport("byLabel", eventKey);
    }

    public List<ObjectInfo> byLabel(String eventKey, String label) {
        return fetchReportFiltered("byLabel", eventKey, label);
    }

    public Map<String, List<ObjectInfo>> byStatus(String eventKey) {
        return fetchReport("byStatus", eventKey);
    }

    public List<ObjectInfo> byStatus(String eventKey, String status) {
        return fetchReportFiltered("byStatus", eventKey, status);
    }

    public Map<String, EventReportSummaryItem> summaryByStatus(String eventKey) {
        return fetchSummaryReport("byStatus", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByStatus(String eventKey) {
        return fetchDeepSummaryReport("byStatus", eventKey);
    }

    public Map<String, List<ObjectInfo>> byObjectType(String eventKey) {
        return fetchReport("byObjectType", eventKey);
    }

    public List<ObjectInfo> byObjectType(String eventKey, String status) {
        return fetchReportFiltered("byObjectType", eventKey, status);
    }

    public Map<String, EventReportSummaryItem> summaryByObjectType(String eventKey) {
        return fetchSummaryReport("byObjectType", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByObjectType(String eventKey) {
        return fetchDeepSummaryReport("byObjectType", eventKey);
    }

    public Map<String, List<ObjectInfo>> byCategoryLabel(String eventKey) {
        return fetchReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryLabel(String eventKey) {
        return fetchDeepSummaryReport("byCategoryLabel", eventKey);
    }

    public List<ObjectInfo> byCategoryLabel(String eventKey, String categorylabel) {
        return fetchReportFiltered("byCategoryLabel", eventKey, categorylabel);
    }

    public Map<String, List<ObjectInfo>> byCategoryKey(String eventKey) {
        return fetchReport("byCategoryKey", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return fetchSummaryReport("byCategoryKey", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByCategoryKey(String eventKey) {
        return fetchDeepSummaryReport("byCategoryKey", eventKey);
    }

    public List<ObjectInfo> byCategoryKey(String eventKey, String categorykey) {
        return fetchReportFiltered("byCategoryKey", eventKey, categorykey);
    }

    public Map<String, List<ObjectInfo>> byOrderId(String eventKey) {
        return fetchReport("byOrderId", eventKey);
    }

    public List<ObjectInfo> byOrderId(String eventKey, String orderId) {
        return fetchReportFiltered("byOrderId", eventKey, orderId);
    }

    public Map<String, List<ObjectInfo>> bySection(String eventKey) {
        return fetchReport("bySection", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryBySection(String eventKey) {
        return fetchDeepSummaryReport("bySection", eventKey);
    }

    public List<ObjectInfo> bySection(String eventKey, String section) {
        return fetchReportFiltered("bySection", eventKey, section);
    }

    public Map<String, List<ObjectInfo>> byChannel(String eventKey) {
        return fetchReport("byChannel", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByChannel(String channelKey) {
        return fetchSummaryReport("byChannel", channelKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryByChannel(String channelKey) {
        return fetchDeepSummaryReport("byChannel", channelKey);
    }

    public List<ObjectInfo> byChannel(String eventKey, String channelKey) {
        return fetchReportFiltered("byChannel", eventKey, channelKey);
    }

    public Map<String, List<ObjectInfo>> bySelectability(String eventKey) {
        return fetchReport("bySelectability", eventKey);
    }

    public List<ObjectInfo> bySelectability(String eventKey, String selectability) {
        return fetchReportFiltered("bySelectability", eventKey, selectability);
    }

    public Map<String, EventReportSummaryItem> summaryBySelectability(String eventKey) {
        return fetchSummaryReport("bySelectability", eventKey);
    }

    public Map<String, EventReportDeepSummaryItem> deepSummaryBySelectability(String eventKey) {
        return fetchDeepSummaryReport("bySelectability", eventKey);
    }

    @Override
    protected TypeToken<Map<String, List<ObjectInfo>>> getTypeToken() {
        return new TypeToken<Map<String, List<ObjectInfo>>>() {
        };
    }

    private Map<String, EventReportSummaryItem> fetchSummaryReport(String reportType, String eventKey) {
        String result = fetchRawSummaryReport(reportType, eventKey);
        TypeToken<Map<String, EventReportSummaryItem>> typeToken = new TypeToken<Map<String, EventReportSummaryItem>>() {
        };
        return gson().fromJson(result, typeToken.getType());
    }

    private String fetchRawSummaryReport(String reportType, String eventKey) {
        return unirest.stringResponse(get(baseUrl + "/reports/events/{key}/{reportType}/summary")
                .routeParam("key", eventKey)
                .routeParam("reportType", reportType));
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

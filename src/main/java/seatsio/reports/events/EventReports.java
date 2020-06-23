package seatsio.reports.events;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import seatsio.reports.Reports;
import seatsio.util.UnirestUtil;

import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class EventReports extends Reports {

    public EventReports(String secretKey, String workspaceKey, String baseUrl) {
        super(secretKey, workspaceKey, baseUrl, "events");
    }

    public Map<String, List<EventReportItem>> byLabel(String eventKey) {
        return fetchReport("byLabel", eventKey);
    }

    public List<EventReportItem> byLabel(String eventKey, String label) {
        return fetchReport("byLabel", eventKey, label);
    }

    public Map<String, List<EventReportItem>> byStatus(String eventKey) {
        return fetchReport("byStatus", eventKey);
    }

    public List<EventReportItem> byStatus(String eventKey, String status) {
        return fetchReport("byStatus", eventKey, status);
    }

    public Map<String, EventReportSummaryItem> summaryByStatus(String eventKey) {
        return fetchSummaryReport("byStatus", eventKey);
    }

    public Map<String, List<EventReportItem>> byCategoryLabel(String eventKey) {
        return fetchReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey);
    }

    public List<EventReportItem> byCategoryLabel(String eventKey, String categorylabel) {
        return fetchReport("byCategoryLabel", eventKey, categorylabel);
    }

    public Map<String, List<EventReportItem>> byCategoryKey(String eventKey) {
        return fetchReport("byCategoryKey", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return fetchSummaryReport("byCategoryKey", eventKey);
    }

    public List<EventReportItem> byCategoryKey(String eventKey, String categorykey) {
        return fetchReport("byCategoryKey", eventKey, categorykey);
    }

    public Map<String, List<EventReportItem>> byOrderId(String eventKey) {
        return fetchReport("byOrderId", eventKey);
    }

    public List<EventReportItem> byOrderId(String eventKey, String orderId) {
        return fetchReport("byOrderId", eventKey, orderId);
    }

    public Map<String, List<EventReportItem>> bySection(String eventKey) {
        return fetchReport("bySection", eventKey);
    }

    public Map<String, EventReportSummaryItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey);
    }

    public List<EventReportItem> bySection(String eventKey, String section) {
        return fetchReport("bySection", eventKey, section);
    }

    public Map<String, List<EventReportItem>> bySelectability(String eventKey) {
        return fetchReport("bySelectability", eventKey);
    }

    public List<EventReportItem> bySelectability(String eventKey, String selectability) {
        return fetchReport("bySelectability", eventKey, selectability);
    }

    public Map<String, EventReportSummaryItem> summaryBySelectability(String eventKey) {
        return fetchSummaryReport("bySelectability", eventKey);
    }

    @Override
    protected TypeToken<Map<String, List<EventReportItem>>> getTypeToken() {
        return new TypeToken<Map<String, List<EventReportItem>>>() {
        };
    }

    private Map<String, EventReportSummaryItem> fetchSummaryReport(String reportType, String eventKey) {
        HttpResponse<String> result = fetchRawSummaryReport(reportType, eventKey);
        TypeToken<Map<String, EventReportSummaryItem>> typeToken = new TypeToken<Map<String, EventReportSummaryItem>>() {
        };
        return gson().fromJson(result.getBody(), typeToken.getType());
    }


    private HttpResponse<String> fetchRawSummaryReport(String reportType, String eventKey) {
        return stringResponse(UnirestUtil.get(baseUrl + "/reports/events/{key}/{reportType}/summary", secretKey, workspaceKey)
                .routeParam("key", eventKey)
                .routeParam("reportType", reportType));
    }

}

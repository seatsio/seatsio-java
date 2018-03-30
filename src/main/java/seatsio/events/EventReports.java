package seatsio.events;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

import java.util.List;
import java.util.Map;

import static com.mashape.unirest.http.Unirest.get;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class EventReports {

    private final String baseUrl;
    private final String secretKey;

    public EventReports(String baseUrl, String secretKey) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
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

    public Map<String, EventSummaryReportItem> summaryByStatus(String eventKey) {
        return fetchSummaryReport("byStatus", eventKey);
    }

    public Map<String, List<EventReportItem>> byCategoryLabel(String eventKey) {
        return fetchReport("byCategoryLabel", eventKey);
    }

    public Map<String, EventSummaryReportItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey);
    }

    public List<EventReportItem> byCategoryLabel(String eventKey, String categorylabel) {
        return fetchReport("byCategoryLabel", eventKey, categorylabel);
    }

    public Map<String, List<EventReportItem>> byCategoryKey(String eventKey) {
        return fetchReport("byCategoryKey", eventKey);
    }

    public Map<String, EventSummaryReportItem> summaryByCategoryKey(String eventKey) {
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

    public Map<String, EventSummaryReportItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey);
    }

    public List<EventReportItem> bySection(String eventKey, String section) {
        return fetchReport("bySection", eventKey, section);
    }

    private <T> Map<T, List<EventReportItem>> fetchReport(String reportType, String eventKey) {
        HttpResponse<String> result = fetchRawReport(reportType, eventKey);
        TypeToken<Map<String, List<EventReportItem>>> typeToken = new TypeToken<Map<String, List<EventReportItem>>>() {
        };
        return gson().fromJson(result.getBody(), typeToken.getType());
    }

    private List<EventReportItem> fetchReport(String reportType, String eventKey, String filter) {
        return fetchReport(reportType, eventKey).get(filter);
    }

    private Map<String, EventSummaryReportItem> fetchSummaryReport(String reportType, String eventKey) {
        HttpResponse<String> result = fetchRawSummaryReport(reportType, eventKey);
        TypeToken<Map<String, EventSummaryReportItem>> typeToken = new TypeToken<Map<String, EventSummaryReportItem>>() {
        };
        return gson().fromJson(result.getBody(), typeToken.getType());
    }

    private HttpResponse<String> fetchRawReport(String reportType, String eventKey) {
        return stringResponse(get(baseUrl + "/reports/events/{key}/{reportType}")
                .basicAuth(secretKey, null)
                .routeParam("key", eventKey)
                .routeParam("reportType", reportType));
    }

    private HttpResponse<String> fetchRawSummaryReport(String reportType, String eventKey) {
        return stringResponse(get(baseUrl + "/reports/events/{key}/{reportType}/summary")
                .basicAuth(secretKey, null)
                .routeParam("key", eventKey)
                .routeParam("reportType", reportType));
    }
}

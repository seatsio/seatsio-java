package seatsio.reports;

import com.google.gson.reflect.TypeToken;
import seatsio.util.UnirestWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gson;

public abstract class Reports {

    protected final String baseUrl;
    private final String reportItemType;
    private final UnirestWrapper unirest;

    public Reports(String baseUrl, String reportItemType, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.reportItemType = reportItemType;
        this.unirest = unirest;
    }

    private String fetchRawReport(String reportType, String key, Map<String, Object> queryParams) {
        return unirest.stringResponse(UnirestWrapper.get(baseUrl + "/reports/" + reportItemType + "/{key}/{reportType}")
                .queryString(queryParams)
                .routeParam("key", key)
                .routeParam("reportType", reportType));
    }

    protected final <T> Map<String, List<T>> fetchReport(String reportType, String key) {
        return fetchReport(reportType, key, null);
    }

    protected final <T> Map<String, List<T>> fetchReport(String reportType, String key, Map<String, Object> queryParams) {
        String result = fetchRawReport(reportType, key, queryParams);
        TypeToken<Map<String, List<T>>> typeToken = getTypeToken();
        return gson().fromJson(result, typeToken.getType());
    }

    protected <T> List<T> fetchReportFiltered(String reportType, String eventKey, String filter) {
        return (List<T>) fetchReport(reportType, eventKey, null).getOrDefault(filter, new ArrayList<>());
    }

    protected abstract <T> TypeToken<Map<String, List<T>>> getTypeToken();
}

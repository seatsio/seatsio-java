package seatsio.reports;

import com.google.gson.reflect.TypeToken;
import seatsio.util.UnirestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public abstract class Reports {

    protected final String secretKey;
    protected final String workspaceKey;
    protected final String baseUrl;
    private final String reportItemType;

    public Reports(String secretKey, String workspaceKey, String baseUrl, String reportItemType) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
        this.reportItemType = reportItemType;
    }

    private String fetchRawReport(String reportType, String key, Map<String, Object> queryParams) {
        return stringResponse(UnirestUtil.get(baseUrl + "/reports/" + reportItemType + "/{key}/{reportType}", secretKey, workspaceKey)
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

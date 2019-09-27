package seatsio.reports;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import seatsio.util.UnirestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public abstract class Reports {

    protected final String secretKey;
    protected final Long accountId;
    protected final String baseUrl;
    private final String reportItemType;

    public Reports(String secretKey, Long accountId, String baseUrl, String reportItemType) {
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.baseUrl = baseUrl;
        this.reportItemType = reportItemType;
    }

    private HttpResponse<String> fetchRawReport(String reportType, String chartKey) {
        return stringResponse(UnirestUtil.get(baseUrl + "/reports/" + reportItemType + "/{key}/{reportType}", secretKey, accountId)
                .routeParam("key", chartKey)
                .routeParam("reportType", reportType));
    }

    protected final <T> Map<String, List<T>> fetchReport(String reportType, String chartKey) {
        HttpResponse<String> result = fetchRawReport(reportType, chartKey);
        TypeToken<Map<String, List<T>>> typeToken = getTypeToken();
        return gson().fromJson(result.getBody(), typeToken.getType());
    }

    protected <T> List<T> fetchReport(String reportType, String eventKey, String filter) {
        return (List<T>) fetchReport(reportType, eventKey).getOrDefault(filter, new ArrayList<>());
    }

    protected abstract <T> TypeToken<Map<String, List<T>>> getTypeToken();
}

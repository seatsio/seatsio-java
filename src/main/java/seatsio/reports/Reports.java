package seatsio.reports;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

import java.util.List;
import java.util.Map;

import static com.mashape.unirest.http.Unirest.get;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public abstract class Reports {

    protected final String secretKey;
    protected final String baseUrl;
    private final String reportItemType;

    public Reports(String secretKey, String baseUrl, String reportItemType) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.reportItemType = reportItemType;
    }

    private HttpResponse<String> fetchRawReport(String reportType, String chartKey) {
        return stringResponse(get(baseUrl + "/reports/" + reportItemType + "/{key}/{reportType}")
                .basicAuth(secretKey, null)
                .routeParam("key", chartKey)
                .routeParam("reportType", reportType));
    }

    protected final <T> Map<String, List<T>> fetchReport(String reportType, String chartKey) {
        HttpResponse<String> result = fetchRawReport(reportType, chartKey);
        TypeToken<Map<String, List<T>>> typeToken = getTypeToken();
        return gson().fromJson(result.getBody(), typeToken.getType());
    }

    protected <T> List<T> fetchReport(String reportType, String eventKey, String filter) {
        return (List<T>) fetchReport(reportType, eventKey).get(filter);
    }

    protected abstract <T> TypeToken<Map<String, List<T>>> getTypeToken();
}

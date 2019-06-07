package seatsio.reports.usage;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObject;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForMonth;

import java.util.List;

import static com.mashape.unirest.http.Unirest.get;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class UsageReports {

    private final String secretKey;
    private final String baseUrl;

    public UsageReports(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public List<UsageSummaryForMonth> summaryForAllMonths() {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/reports/usage")
                .basicAuth(secretKey, null));
        TypeToken<List<UsageSummaryForMonth>> typeToken = new TypeToken<List<UsageSummaryForMonth>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }

    public List<UsageDetails> detailsForMonth(Month month) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/reports/usage/month/{month}")
                .basicAuth(secretKey, null)
                .routeParam("month", month.serialize()));
        TypeToken<List<UsageDetails>> typeToken = new TypeToken<List<UsageDetails>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }

    public List<UsageForObject> detailsForEventInMonth(long eventId, Month month) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/reports/usage/month/{month}/event/{eventId}")
                .basicAuth(secretKey, null)
                .routeParam("month", month.serialize())
                .routeParam("eventId", Long.toString(eventId)));
        TypeToken<List<UsageForObject>> typeToken = new TypeToken<List<UsageForObject>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }
}

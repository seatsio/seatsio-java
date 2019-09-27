package seatsio.reports.usage;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObject;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForMonth;
import seatsio.util.UnirestUtil;

import java.util.List;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class UsageReports {

    private final String secretKey;
    private final Long accountId;
    private final String baseUrl;

    public UsageReports(String secretKey, Long accountId, String baseUrl) {
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.baseUrl = baseUrl;
    }

    public List<UsageSummaryForMonth> summaryForAllMonths() {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/reports/usage", secretKey, accountId));
        TypeToken<List<UsageSummaryForMonth>> typeToken = new TypeToken<List<UsageSummaryForMonth>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }

    public List<UsageDetails> detailsForMonth(Month month) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/reports/usage/month/{month}", secretKey, accountId)
                .routeParam("month", month.serialize()));
        TypeToken<List<UsageDetails>> typeToken = new TypeToken<List<UsageDetails>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }

    public List<UsageForObject> detailsForEventInMonth(long eventId, Month month) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/reports/usage/month/{month}/event/{eventId}", secretKey, accountId)
                .routeParam("month", month.serialize())
                .routeParam("eventId", Long.toString(eventId)));
        TypeToken<List<UsageForObject>> typeToken = new TypeToken<List<UsageForObject>>() {
        };
        return gson().fromJson(response.getBody(), typeToken.getType());
    }
}

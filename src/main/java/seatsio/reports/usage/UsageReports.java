package seatsio.reports.usage;

import com.google.gson.reflect.TypeToken;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObject;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForMonth;
import seatsio.util.UnirestWrapper;

import java.util.List;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.get;

public class UsageReports {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public UsageReports(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public List<UsageSummaryForMonth> summaryForAllMonths() {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage"));
        TypeToken<List<UsageSummaryForMonth>> typeToken = new TypeToken<List<UsageSummaryForMonth>>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    public List<UsageDetails> detailsForMonth(Month month) {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage/month/{month}")
                .routeParam("month", month.serialize()));
        TypeToken<List<UsageDetails>> typeToken = new TypeToken<List<UsageDetails>>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    public List<UsageForObject> detailsForEventInMonth(long eventId, Month month) {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage/month/{month}/event/{eventId}")
                .routeParam("month", month.serialize())
                .routeParam("eventId", Long.toString(eventId)));
        TypeToken<List<UsageForObject>> typeToken = new TypeToken<List<UsageForObject>>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }
}

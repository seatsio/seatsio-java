package seatsio.reports.usage;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObjectV1;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObjectV2;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForAllMonths;
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

    public UsageSummaryForAllMonths summaryForAllMonths() {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage?version=2"));
        return gson().fromJson(response, UsageSummaryForAllMonths.class);
    }

    public List<UsageDetails> detailsForMonth(Month month) {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage/month/{month}")
                .routeParam("month", month.serialize()));
        TypeToken<List<UsageDetails>> typeToken = new TypeToken<>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    /**
     * @return a list of UsageForObjectV1 objects (for usage until November 2022) or UsageForObjectV2 objects (from December 2022)
     */
    public List<?> detailsForEventInMonth(long eventId, Month month) {
        String response = unirest.stringResponse(get(baseUrl + "/reports/usage/month/{month}/event/{eventId}")
                .routeParam("month", month.serialize())
                .routeParam("eventId", Long.toString(eventId)));
        JsonArray json = JsonParser.parseString(response).getAsJsonArray();
        if (!json.isEmpty() && json.get(0).getAsJsonObject().has("usageByReason")) {
            return detailsForEventInMonthV2(response);
        }
        return detailsForEventInMonthV1(response);
    }

    private static List<UsageForObjectV1> detailsForEventInMonthV1(String response) {
        TypeToken<List<UsageForObjectV1>> typeToken = new TypeToken<>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    private static List<UsageForObjectV2> detailsForEventInMonthV2(String response) {
        TypeToken<List<UsageForObjectV2>> typeToken = new TypeToken<>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }
}

package seatsio.seasons;

import seatsio.json.JsonObjectBuilder;
import seatsio.util.UnirestWrapper;

import java.util.List;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;

public class Seasons {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public Seasons(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public Season create(String chartKey) {
        return create(chartKey, null, null, null);
    }

    public Season create(String chartKey, String key, List<String> eventKeys) {
        return create(chartKey, key, eventKeys, null);
    }

    public Season create(String chartKey, String key, int numberOfEvents) {
        return create(chartKey, key, null, numberOfEvents);
    }

    public Season create(String chartKey, String key, List<String> eventKeys, Integer numberOfEvents) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("key", key)
                .withPropertyIfNotNull("eventKeys", eventKeys)
                .withPropertyIfNotNull("numberOfEvents", numberOfEvents);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons")
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Season createPartialSeason(String chartKey, String topLevelSeasonKey, String key, List<String> eventKeys) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("key", key)
                .withPropertyIfNotNull("eventKeys", eventKeys);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{topLevelSeasonKey}/partial-seasons")
                .routeParam("topLevelSeasonKey", topLevelSeasonKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }
}

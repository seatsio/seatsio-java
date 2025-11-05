package seatsio.seasons;

import seatsio.SeatsioClient;
import seatsio.charts.CategoryKey;
import seatsio.events.Event;
import seatsio.events.EventCreationResult;
import seatsio.events.UpdateEventParams;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.UnirestWrapper;

import java.util.List;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.post;

public class Seasons {

    private final String baseUrl;
    private final UnirestWrapper unirest;
    private final SeatsioClient seatsioClient;

    public Seasons(String baseUrl, UnirestWrapper unirest, SeatsioClient seatsioClient) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
        this.seatsioClient = seatsioClient;
    }

    public Season create(String chartKey) {
        return create(chartKey, new CreateSeasonParams());
    }

    public Season create(String chartKey, CreateSeasonParams seasonParams) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("key", seasonParams.key())
                .withPropertyIfNotNull("name", seasonParams.name())
                .withPropertyIfNotNull("eventKeys", seasonParams.eventKeys())
                .withPropertyIfNotNull("numberOfEvents", seasonParams.numberOfEvents())
                .withPropertyIfNotNull("tableBookingConfig", seasonParams.tableBookingConfig())
                .withPropertyIfNotNull("channels", seasonParams.getChannelsAsJson())
                .withPropertyIfNotNull("forSaleConfig", seasonParams.getForSaleConfigAsJson())
                .withPropertyIfNotNull("forSalePropagated", seasonParams.forSalePropagated());
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons")
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public List<Event> createEvents(String key, List<String> eventKeys) {
        return doCreateEvents(key, eventKeys, null);
    }

    public List<Event> createEvents(String key, int numberOfEvents) {
        return doCreateEvents(key, null, numberOfEvents);
    }

    private List<Event> doCreateEvents(String key, List<String> eventKeys, Integer numberOfEvents) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("eventKeys", eventKeys)
                .withPropertyIfNotNull("numberOfEvents", numberOfEvents);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{key}/actions/create-events")
                .routeParam("key", key)
                .body(request.build().toString()));
        return gson().fromJson(response, EventCreationResult.class).events;
    }

    public Season createPartialSeason(String topLevelSeasonKey, String key, String name, List<String> eventKeys) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("key", key)
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("eventKeys", eventKeys);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{topLevelSeasonKey}/partial-seasons")
                .routeParam("topLevelSeasonKey", topLevelSeasonKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Season removeEventFromPartialSeason(String topLevelSeasonKey, String partialSeasonKey, String eventKey) {
        String response = unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/seasons/{topLevelSeasonKey}/partial-seasons/{partialSeasonKey}/events/{eventKey}")
                .routeParam("topLevelSeasonKey", topLevelSeasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey)
                .routeParam("eventKey", eventKey));
        return gson().fromJson(response, Season.class);
    }

    public Season addEventsToPartialSeason(String topLevelSeasonKey, String partialSeasonKey, List<String> eventKeys) {
        JsonObjectBuilder request = aJsonObject().withProperty("eventKeys", eventKeys);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{topLevelSeasonKey}/partial-seasons/{partialSeasonKey}/actions/add-events")
                .routeParam("topLevelSeasonKey", topLevelSeasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Season retrieve(String key) {
        return (Season) seatsioClient.events.retrieve(key);
    }

    public void update(String eventKey, UpdateSeasonParams params) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("eventKey", params.key())
                .withPropertyIfNotNull("tableBookingConfig", params.tableBookingConfig())
                .withPropertyIfNotNull("name", params.name())
                .withPropertyIfNotNull("forSalePropagated", params.forSalePropagated());
        unirest.stringResponse(post(baseUrl + "/events/{key}")
                .routeParam("key", eventKey)
                .body(request.build().toString()));
    }

}

package seatsio.seasons;

import seatsio.events.TableBookingConfig;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;
import seatsio.util.UnirestWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        return create(chartKey, null, null, null, null, null);
    }

    public Season create(String chartKey, String key, List<String> eventKeys, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey) {
        return create(chartKey, key, eventKeys, null, tableBookingConfig, socialDistancingRulesetKey);
    }

    public Season create(String chartKey, String key, int numberOfEvents, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey) {
        return create(chartKey, key, null, numberOfEvents, tableBookingConfig, socialDistancingRulesetKey);
    }

    public Season create(String chartKey, String key, List<String> eventKeys, Integer numberOfEvents, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("key", key)
                .withPropertyIfNotNull("eventKeys", eventKeys)
                .withPropertyIfNotNull("numberOfEvents", numberOfEvents)
                .withPropertyIfNotNull("tableBookingConfig", tableBookingConfig)
                .withPropertyIfNotNull("socialDistancingRulesetKey", socialDistancingRulesetKey);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons")
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Season createPartialSeason(String topLevelSeasonKey, String key, List<String> eventKeys) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("key", key)
                .withPropertyIfNotNull("eventKeys", eventKeys);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{topLevelSeasonKey}/partial-seasons")
                .routeParam("topLevelSeasonKey", topLevelSeasonKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Stream<Season> listAll() {
        return list().all();
    }

    public Page<Season> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Season> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Season> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Season> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Season> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Season> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    private Lister<Season> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/seasons", unirest, Season.class));
    }

    public Season retrieve(String key) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/seasons/{key}")
                .routeParam("key", key));
        return gson().fromJson(response, Season.class);
    }

    public Season retrievePartialSeason(String seasonKey, String partialSeasonKey) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/seasons/{seasonKey}/partial-seasons/{partialSeasonKey}")
                .routeParam("seasonKey", seasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey));
        return gson().fromJson(response, Season.class);
    }

    public void delete(String key) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/seasons/{key}")
                .routeParam("key", key));
    }

    public void deletePartialSeason(String seasonKey, String partialSeasonKey) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/seasons/{seasonKey}/partial-seasons/{partialSeasonKey}")
                .routeParam("seasonKey", seasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey));
    }

    public Season createEvents(String key, List<String> eventKeys) {
        return doCreateEvents(key, eventKeys, null);
    }

    public Season createEvents(String key, int numberOfEvents) {
        return doCreateEvents(key, null, numberOfEvents);
    }

    private Season doCreateEvents(String key, List<String> eventKeys, Integer numberOfEvents) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("eventKeys", eventKeys)
                .withPropertyIfNotNull("numberOfEvents", numberOfEvents);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{key}/actions/create-events")
                .routeParam("key", key)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }

    public Season removeEventFromPartialSeason(String seasonKey, String partialSeasonKey, String eventKey) {
        String response = unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/seasons/{seasonKey}/partial-seasons/{partialSeasonKey}/events/{eventKey}")
                .routeParam("seasonKey", seasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey)
                .routeParam("eventKey", eventKey));
        return gson().fromJson(response, Season.class);
    }

    public Season addEventsToPartialSeason(String seasonKey, String partialSeasonKey, ArrayList<String> eventKeys) {
        JsonObjectBuilder request = aJsonObject().withProperty("eventKeys", eventKeys);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/seasons/{seasonKey}/partial-seasons/{partialSeasonKey}/actions/add-events")
                .routeParam("seasonKey", seasonKey)
                .routeParam("partialSeasonKey", partialSeasonKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Season.class);
    }
}

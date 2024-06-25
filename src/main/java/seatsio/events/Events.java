package seatsio.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import seatsio.SortDirection;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static seatsio.events.EventObjectInfo.*;
import static seatsio.json.JsonArrayBuilder.aJsonArray;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.post;

public class Events {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public final Channels channels;

    public Events(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
        this.channels = new Channels(baseUrl, unirest);
    }

    public Event create(String chartKey) {
        return create(chartKey, new CreateEventParams());
    }

    public Event create(String chartKey, CreateEventParams params) {
        String request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("eventKey", params.eventKey)
                .withPropertyIfNotNull("name", params.name)
                .withPropertyIfNotNull("date", params.date == null ? null : gson().toJsonTree(params.date))
                .withPropertyIfNotNull("tableBookingConfig", params.tableBookingConfig)
                .withPropertyIfNotNull("objectCategories", params.objectCategories, CategoryKey::toJson)
                .withPropertyIfNotNull("categories", params.getCategoriesAsJson())
                .withPropertyIfNotNull("channels", params.getChannelsAsJson())
                .withPropertyIfNotNull("forSaleConfig", params.getForSaleConfigAsJson())
                .buildAsString();

        String response = unirest.stringResponse(post(baseUrl + "/events").body(request));
        return gson().fromJson(response, Event.class);
    }

    public List<Event> create(String chartKey, Collection<CreateEventParams> params) {
        JsonArray events = new JsonArray();
        new ArrayList<>(params).forEach(p -> events.add(aJsonObject()
                .withPropertyIfNotNull("eventKey", p.eventKey)
                .withPropertyIfNotNull("name", p.name)
                .withPropertyIfNotNull("date", p.date == null ? null : gson().toJsonTree(p.date))
                .withPropertyIfNotNull("tableBookingConfig", p.tableBookingConfig)
                .withPropertyIfNotNull("objectCategories", p.objectCategories, CategoryKey::toJson)
                .withPropertyIfNotNull("categories", p.getCategoriesAsJson())
                .withPropertyIfNotNull("channels", p.getChannelsAsJson())
                .withPropertyIfNotNull("forSaleConfig", p.getForSaleConfigAsJson())
                .build()));
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withProperty("events", events);

        String response = unirest.stringResponse(post(baseUrl + "/events/actions/create-multiple").body(request.build().toString()));

        return gson().fromJson(response, EventCreationResult.class).events;
    }

    public void removeObjectCategories(String eventKey) {
        update(eventKey, new UpdateEventParams().withObjectCategories(Map.of()));
    }

    public void removeCategories(String eventKey) {
        update(eventKey, new UpdateEventParams().withCategories(new ArrayList<>()));
    }

    public void update(String key, UpdateEventParams params) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("chartKey", params.chartKey)
                .withPropertyIfNotNull("eventKey", params.eventKey)
                .withPropertyIfNotNull("name", params.name)
                .withPropertyIfNotNull("date", params.date == null ? null : gson().toJsonTree(params.date))
                .withPropertyIfNotNull("tableBookingConfig", params.tableBookingConfig)
                .withPropertyIfNotNull("objectCategories", params.objectCategories, CategoryKey::toJson)
                .withPropertyIfNotNull("categories", categoriesAsJson(params.categories))
                .withPropertyIfNotNull("isInThePast", params.isInThePast);
        unirest.stringResponse(post(baseUrl + "/events/{key}")
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    private List<JsonObject> categoriesAsJson(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream().map(Category::toJson).collect(toList());
    }

    public void delete(String key) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/events/{key}")
                .routeParam("key", key));
    }

    public Event retrieve(String key) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/events/{key}")
                .routeParam("key", key));
        return gson().fromJson(response, Event.class);
    }

    public void markAsForSale(String key, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/mark-as-for-sale")
                .routeParam("key", key)
                .body(forSaleRequest(objects, areaPlaces, categories).toString()));
    }

    public void markAsNotForSale(String key, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/mark-as-not-for-sale")
                .routeParam("key", key)
                .body(forSaleRequest(objects, areaPlaces, categories).toString()));
    }

    private JsonObject forSaleRequest(List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        return aJsonObject()
                .withPropertyIfNotNull("objects", objects)
                .withPropertyIfNotNull("areaPlaces", areaPlaces)
                .withPropertyIfNotNull("categories", categories)
                .build();
    }

    public void markEverythingAsForSale(String key) {
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/mark-everything-as-for-sale")
                .routeParam("key", key));
    }

    public Stream<Event> listAll() {
        return list().all();
    }

    public Page<Event> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Event> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Event> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Event> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Event> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Event> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    private Lister<Event> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/events", unirest, Event.class));
    }

    public Lister<StatusChange> statusChanges(String eventKey) {
        return statusChanges(eventKey, null, null, null);
    }

    public Lister<StatusChange> statusChanges(String eventKey, String filter) {
        return statusChanges(eventKey, filter, null, null);
    }

    public Lister<StatusChange> statusChanges(String eventKey, String filter, String sortField, SortDirection sortDirection) {
        PageFetcher<StatusChange> pageFetcher = new PageFetcher<>(
                baseUrl,
                "/events/{key}/status-changes", Map.of("key", eventKey), new HashMap<>() {{
            put("filter", filter);
            put("sort", toSort(sortField, sortDirection));
        }}, unirest, StatusChange.class);
        return new Lister<>(pageFetcher);
    }

    private String toSort(String sortField, SortDirection sortDirection) {
        if (sortField == null) {
            return null;
        }
        if (sortDirection == null) {
            return sortField;
        }
        return sortField + ":" + sortDirection.name();
    }

    public Lister<StatusChange> statusChangesForObject(String key, String objectId) {
        PageFetcher<StatusChange> pageFetcher = new PageFetcher<>(
                baseUrl,
                "/events/{key}/objects/{objectId}/status-changes", Map.of("key", key, "objectId", objectId),
                unirest,
                StatusChange.class);
        return new Lister<>(pageFetcher);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects) {
        return book(eventKey, objects, null, null, null, null, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken) {
        return book(eventKey, objects, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, objects, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable) {
        return book(eventKey, bestAvailable, null, null, null, null, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable, String holdToken) {
        return book(eventKey, bestAvailable, holdToken, null, null, null, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, bestAvailable, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult book(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKeys, objects, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken) {
        return hold(eventKey, objects, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, objects, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken) {
        return hold(eventKey, bestAvailable, holdToken, null, null, null, null);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, bestAvailable, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult hold(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKeys, objects, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects) {
        return release(eventKey, objects, null, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken) {
        return release(eventKey, objects, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, objects, FREE, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult release(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKeys, objects, FREE, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status) {
        return changeObjectStatus(eventKey, bestAvailable, status, null, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken) {
        return changeObjectStatus(eventKey, bestAvailable, status, holdToken, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        String result = unirest.stringResponse(post(baseUrl + "/events/{key}/actions/change-object-status")
                .routeParam("key", eventKey)
                .body(changeObjectStatusRequest(bestAvailable, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys).toString()));
        return gson().fromJson(result, BestAvailableResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status) {
        return changeObjectStatus(eventKey, objects, status, null, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken) {
        return changeObjectStatus(eventKey, objects, status, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status) {
        return changeObjectStatus(eventKeys, objects, status, null, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        String response = unirest.stringResponse(post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null).toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        String response = unirest.stringResponse(post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses).toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public void overrideSeasonObjectStatus(String eventKey, List<String> objects) {
        unirest.stringResponse(post(baseUrl + "/events/{eventKey}/actions/override-season-status")
                .routeParam("eventKey", eventKey)
                .body(useOrOverrideSeasonObjectStatusRequest(objects).toString()));
    }

    public void useSeasonObjectStatus(String eventKey, List<String> objects) {
        unirest.stringResponse(post(baseUrl + "/events/{eventKey}/actions/use-season-status")
                .routeParam("eventKey", eventKey)
                .body(useOrOverrideSeasonObjectStatusRequest(objects).toString()));
    }

    private static JsonObject useOrOverrideSeasonObjectStatusRequest(List<String> objects) {
        return aJsonObject()
                .withProperty("objects", aJsonArray().withItems(objects.toArray(new String[]{})).build())
                .build();
    }

    public List<ChangeObjectStatusResult> changeObjectStatus(List<StatusChangeRequest> statusChangeRequests) {
        List<JsonElement> statusChangeRequestsAsJson = statusChangeRequests
                .stream()
                .map(s -> changeObjectStatusRequest(s.eventKey, toObjects(s.objects), s.status, s.holdToken, s.orderId, s.keepExtraData, s.ignoreChannels, s.channelKeys, s.allowedPreviousStatuses, s.rejectedPreviousStatuses))
                .collect(toList());
        JsonObject request = aJsonObject()
                .withProperty("statusChanges", aJsonArray().withItems(statusChangeRequestsAsJson).build())
                .build();
        String response = unirest.stringResponse(post(baseUrl + "/events/actions/change-object-status")
                .queryString("expand", "objects")
                .body(request.toString()));
        return gson().fromJson(response, ChangeObjectStatusInBatchResult.class).results;
    }

    private static class ChangeObjectStatusInBatchResult extends ValueObject {

        public List<ChangeObjectStatusResult> results;

    }

    private List<ObjectProperties> toObjects(List<?> objects) {
        return objects.stream()
                .map(ObjectProperties::from)
                .collect(toList());
    }

    private JsonObject changeObjectStatusRequest(String eventKey, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses);
        request.withProperty("event", eventKey);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(List<String> eventKeys, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses);
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(BestAvailable bestAvailable, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null);
        request.withProperty("bestAvailable", gson().toJsonTree(bestAvailable));
        return request.build();
    }

    private JsonObjectBuilder changeObjectStatusRequestBuilder(String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        return aJsonObject()
                .withProperty("status", status)
                .withPropertyIfNotNull("holdToken", holdToken)
                .withPropertyIfNotNull("orderId", orderId)
                .withPropertyIfNotNull("keepExtraData", keepExtraData)
                .withPropertyIfNotNull("ignoreChannels", ignoreChannels)
                .withPropertyIfNotNull("channelKeys", channelKeys)
                .withPropertyIfNotNull("allowedPreviousStatuses", allowedPreviousStatuses)
                .withPropertyIfNotNull("rejectedPreviousStatuses", rejectedPreviousStatuses);
    }

    public EventObjectInfo retrieveObjectInfo(String key, String label) {
        return retrieveObjectInfos(key, List.of(label)).get(label);
    }

    public Map<String, EventObjectInfo> retrieveObjectInfos(String key, List<String> labels) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/events/{key}/objects")
                .queryString("label", labels)
                .routeParam("key", key));
        TypeToken<Map<String, EventObjectInfo>> typeToken = new TypeToken<>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    public void updateExtraData(String key, String object, Map<String, Object> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(post(baseUrl + "/events/{key}/objects/{object}/actions/update-extra-data")
                .routeParam("key", key)
                .routeParam("object", object)
                .body(request.build().toString()));
    }

    public void updateExtraDatas(String key, Map<String, Map<String, Object>> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/update-extra-data")
                .routeParam("key", key)
                .body(request.build().toString()));
    }

}

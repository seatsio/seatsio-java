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
import static java.util.stream.Collectors.toMap;
import static seatsio.events.EventObjectInfo.*;
import static seatsio.events.StatusChangeType.CHANGE_STATUS_TO;
import static seatsio.events.StatusChangeType.RELEASE;
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

    public void update(String eventKey, UpdateEventParams params) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("eventKey", params.eventKey)
                .withPropertyIfNotNull("name", params.name)
                .withPropertyIfNotNull("date", params.date == null ? null : gson().toJsonTree(params.date))
                .withPropertyIfNotNull("tableBookingConfig", params.tableBookingConfig)
                .withPropertyIfNotNull("objectCategories", params.objectCategories, CategoryKey::toJson)
                .withPropertyIfNotNull("categories", categoriesAsJson(params.categories))
                .withPropertyIfNotNull("isInThePast", params.isInThePast);
        unirest.stringResponse(post(baseUrl + "/events/{key}")
                .routeParam("key", eventKey)
                .body(request.build().toString()));
    }

    private List<JsonObject> categoriesAsJson(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream().map(Category::toJson).collect(toList());
    }

    public void delete(String eventKey) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/events/{key}")
                .routeParam("key", eventKey));
    }

    public Event retrieve(String eventKey) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/events/{key}")
                .routeParam("key", eventKey));
        return gson().fromJson(response, Event.class);
    }

    public ForSaleConfig editForSaleConfig(String eventKey, List<ObjectAndQuantity> forSale, List<ObjectAndQuantity> notForSale) {
        String response = unirest.stringResponse(post(baseUrl + "/events/{key}/actions/edit-for-sale-config")
                .routeParam("key", eventKey)
                .body(editForSaleConfigRequest(forSale, notForSale).toString()));
        return gson().fromJson(response, EditForSaleConfigResponse.class).forSaleConfig;
    }

    public Map<String, ForSaleConfig> editForSaleConfigForEvents(Map<String, ForSaleAndNotForSaleParams> events) {
        String response = unirest.stringResponse(post(baseUrl + "/events/actions/edit-for-sale-config")
                .body(editForSaleConfigForEventsRequest(events).toString()));
        var typeToken = new TypeToken<Map<String, EditForSaleConfigResponse>>() {
        };
        Map<String, EditForSaleConfigResponse> parsedResponse = gson().fromJson(response, typeToken.getType());
        return parsedResponse.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().forSaleConfig));
    }

    public void replaceForSaleConfig(boolean forSale, String eventKey, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        String action = forSale ? "mark-as-for-sale" : "mark-as-not-for-sale";
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/" + action)
                .routeParam("key", eventKey)
                .body(forSaleRequest(objects, areaPlaces, categories).toString()));
    }

    /**
     * @deprecated Use {@link #replaceForSaleConfig(boolean, String, List, Map, List)} instead.
     */
    @Deprecated
    public void markAsForSale(String eventKey, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        replaceForSaleConfig(true, eventKey, objects, areaPlaces, categories);
    }

    /**
     * @deprecated Use {@link #replaceForSaleConfig(boolean, String, List, Map, List)} instead.
     */
    @Deprecated
    public void markAsNotForSale(String eventKey, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        replaceForSaleConfig(false, eventKey, objects, areaPlaces, categories);
    }
    private JsonObject forSaleRequest(List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        return aJsonObject()
                .withPropertyIfNotNull("objects", objects)
                .withPropertyIfNotNull("areaPlaces", areaPlaces)
                .withPropertyIfNotNull("categories", categories)
                .build();
    }

    private JsonObject editForSaleConfigRequest(List<ObjectAndQuantity> forSale, List<ObjectAndQuantity> notForSale) {
        return aJsonObject()
                .withPropertyIfNotNull("forSale", forSale)
                .withPropertyIfNotNull("notForSale", notForSale)
                .build();
    }

    private JsonObject editForSaleConfigForEventsRequest(Map<String, ForSaleAndNotForSaleParams> events) {
        return aJsonObject().withProperty("events", events).build();
    }

    public void markEverythingAsForSale(String eventKey) {
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/mark-everything-as-for-sale")
                .routeParam("key", eventKey));
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

    public Lister<StatusChange> statusChangesForObject(String eventKey, String objectId) {
        PageFetcher<StatusChange> pageFetcher = new PageFetcher<>(
                baseUrl,
                "/events/{key}/objects/{objectId}/status-changes", Map.of("key", eventKey, "objectId", objectId),
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

    public BestAvailableResult book(String eventKey, BestAvailableParams bestAvailableParams) {
        return book(eventKey, bestAvailableParams, null, null, null, null, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailableParams bestAvailableParams, String holdToken) {
        return book(eventKey, bestAvailableParams, holdToken, null, null, null, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailableParams bestAvailableParams, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, bestAvailableParams, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
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

    public BestAvailableResult hold(String eventKey, BestAvailableParams bestAvailableParams, String holdToken) {
        return hold(eventKey, bestAvailableParams, holdToken, null, null, null, null);
    }

    public BestAvailableResult hold(String eventKey, BestAvailableParams bestAvailableParams, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, bestAvailableParams, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult hold(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKeys, objects, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult putUpForResale(String eventKey, List<?> objects, String resaleListingId) {
        return changeObjectStatus(eventKey, objects, RESALE, null, null, null, null, null, null, null, resaleListingId);
    }

    public ChangeObjectStatusResult putUpForResale(List<String> eventKeys, List<?> objects, String resaleListingId) {
        return changeObjectStatus(eventKeys, objects, RESALE, null, null, null, null, null, null, null, resaleListingId);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects) {
        return release(eventKey, objects, null, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken) {
        return release(eventKey, objects, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return releaseObjects(singletonList(eventKey), objects, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult release(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return releaseObjects(eventKeys, objects, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    private ChangeObjectStatusResult releaseObjects(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        JsonObject body = releaseObjectsRequest(eventKeys, toObjects(objects), holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null);
        String response = unirest.stringResponse(post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(body.toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailableParams bestAvailableParams, String status) {
        return changeObjectStatus(eventKey, bestAvailableParams, status, null, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailableParams bestAvailableParams, String status, String holdToken) {
        return changeObjectStatus(eventKey, bestAvailableParams, status, holdToken, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailableParams bestAvailableParams, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        String result = unirest.stringResponse(post(baseUrl + "/events/{key}/actions/change-object-status")
                .routeParam("key", eventKey)
                .body(changeObjectStatusRequest(bestAvailableParams, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys).toString()));
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

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, resaleListingId);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status) {
        return changeObjectStatus(eventKeys, objects, status, null, null, null, null, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        String response = unirest.stringResponse(post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(CHANGE_STATUS_TO, eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null, null).toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        String response = unirest.stringResponse(post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(CHANGE_STATUS_TO, eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, resaleListingId).toString()));
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
                .map(s -> changeObjectStatusRequest(s.type, s.eventKey, toObjects(s.objects), s.status, s.holdToken, s.orderId, s.keepExtraData, s.ignoreChannels, s.channelKeys, s.allowedPreviousStatuses, s.rejectedPreviousStatuses, s.resaleListingId))
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

    private JsonObject changeObjectStatusRequest(StatusChangeType type, String eventKey, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(type, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, resaleListingId);
        request.withProperty("event", eventKey);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(StatusChangeType type, List<String> eventKeys, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(type, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, resaleListingId);
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(BestAvailableParams bestAvailableParams, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(CHANGE_STATUS_TO, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null, null);
        request.withProperty("bestAvailable", gson().toJsonTree(bestAvailableParams));
        return request.build();
    }

    private JsonObject releaseObjectsRequest(List<String> eventKeys, List<ObjectProperties> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(RELEASE, null, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, null);
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObjectBuilder changeObjectStatusRequestBuilder(StatusChangeType type, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        return aJsonObject()
                .withProperty("type", type)
                .withPropertyIfNotNull("status", type == RELEASE ? null : status)
                .withPropertyIfNotNull("holdToken", holdToken)
                .withPropertyIfNotNull("orderId", orderId)
                .withPropertyIfNotNull("keepExtraData", keepExtraData)
                .withPropertyIfNotNull("ignoreChannels", ignoreChannels)
                .withPropertyIfNotNull("channelKeys", channelKeys)
                .withPropertyIfNotNull("allowedPreviousStatuses", allowedPreviousStatuses)
                .withPropertyIfNotNull("rejectedPreviousStatuses", rejectedPreviousStatuses)
                .withPropertyIfNotNull("resaleListingId", resaleListingId);
    }

    public EventObjectInfo retrieveObjectInfo(String eventKey, String label) {
        return retrieveObjectInfos(eventKey, List.of(label)).get(label);
    }

    public Map<String, EventObjectInfo> retrieveObjectInfos(String eventKey, List<String> labels) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/events/{key}/objects")
                .queryString("label", labels)
                .routeParam("key", eventKey));
        TypeToken<Map<String, EventObjectInfo>> typeToken = new TypeToken<>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    public void updateExtraData(String eventKey, String object, Map<String, Object> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(post(baseUrl + "/events/{key}/objects/{object}/actions/update-extra-data")
                .routeParam("key", eventKey)
                .routeParam("object", object)
                .body(request.build().toString()));
    }

    public void updateExtraDatas(String eventKey, Map<String, Map<String, Object>> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(post(baseUrl + "/events/{key}/actions/update-extra-data")
                .routeParam("key", eventKey)
                .body(request.build().toString()));
    }

    private static class EditForSaleConfigResponse {
        ForSaleConfig forSaleConfig;
    }
}

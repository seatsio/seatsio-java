package seatsio.events;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import seatsio.SeatsioException;
import seatsio.SortDirection;
import seatsio.charts.CategoryKey;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.*;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static seatsio.events.EventObjectInfo.*;
import static seatsio.json.JsonArrayBuilder.aJsonArray;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;

public class Events {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public Events(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public Event create(String chartKey) {
        return create(chartKey, null, null, null);
    }

    public Event create(String chartKey, String eventKey) {
        return create(chartKey, eventKey, null, null);
    }

    public Event create(String chartKey, String eventKey, TableBookingConfig tableBookingConfig) {
        return create(chartKey, eventKey, tableBookingConfig, null);
    }

    // TODO remove
    public Event create(String chartKey, String eventKey, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("eventKey", eventKey)
                .withPropertyIfNotNull("tableBookingConfig", tableBookingConfig)
                .withPropertyIfNotNull("socialDistancingRulesetKey", socialDistancingRulesetKey);
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events")
                .body(request.build().toString()));
        return gson().fromJson(response, Event.class);
    }

    public Event create(String chartKey, String eventKey, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey, Map<String, CategoryKey> objectCategories) {
        String request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withPropertyIfNotNull("eventKey", eventKey)
                .withPropertyIfNotNull("tableBookingConfig", tableBookingConfig)
                .withPropertyIfNotNull("socialDistancingRulesetKey", socialDistancingRulesetKey)
                .withPropertyIfNotNull("objectCategories", objectCategories, CategoryKey::toString)
                .buildAsString();
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events")
                .body(request));
        return gson().fromJson(response, Event.class);
    }

    public List<Event> create(String chartKey, List<EventCreationParams> params) {
        JsonArray events = new JsonArray();
        params.forEach(p -> events.add(aJsonObject()
                .withPropertyIfNotNull("eventKey", p.eventKey)
                .withPropertyIfNotNull("tableBookingConfig", p.tableBookingConfig)
                .withPropertyIfNotNull("socialDistancingRulesetKey", p.socialDistancingRulesetKey)
                .build()));
        JsonObjectBuilder request = aJsonObject()
                .withProperty("chartKey", chartKey)
                .withProperty("events", events);

        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/actions/create-multiple")
                .body(request.build().toString()));

        return gson().fromJson(response, EventCreationResult.class).events;
    }

    public void update(String key, String chartKey, String newKey) {
        update(key, chartKey, newKey, null, null);
    }


    public void update(String key, String chartKey, String newKey, TableBookingConfig tableBookingConfig) {
        update(key, chartKey, newKey, tableBookingConfig, null);
    }

    public void update(String key, String chartKey, String newKey, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("chartKey", chartKey)
                .withPropertyIfNotNull("eventKey", newKey)
                .withPropertyIfNotNull("tableBookingConfig", tableBookingConfig)
                .withPropertyIfNotNull("socialDistancingRulesetKey", socialDistancingRulesetKey);
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}")
                .routeParam("key", key)
                .body(request.build().toString()));
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

    public void updateChannels(String key, Map<String, Channel> channels) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels/update")
                .routeParam("key", key)
                .body(updateChannelsRequest(channels))
        );
    }

    private String updateChannelsRequest(Map<String, Channel> channels) {
        return aJsonObject()
                .withProperty("channels", aJsonObject()
                        .withProperties(channels, channel -> aJsonObject()
                                .withProperty("name", channel.name)
                                .withProperty("color", channel.color)
                                .withProperty("index", channel.index)
                                .build())
                        .build()
                ).buildAsString();
    }

    public void assignObjectsToChannel(String key, Map<String, Set<String>> channelKeysAndObjectLabels) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels/assign-objects")
                .routeParam("key", key)
                .body(assignChannelsRequest(channelKeysAndObjectLabels))
        );
    }

    private String assignChannelsRequest(Map<String, Set<String>> channelKeysAndObjectLabels) {
        JsonObjectBuilder config = aJsonObject();
        channelKeysAndObjectLabels.forEach(config::withProperty);
        return aJsonObject().withProperty("channelConfig", config.build()).buildAsString();
    }

    public void markAsForSale(String key, List<String> objects, List<String> categories) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/actions/mark-as-for-sale")
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString()));
    }

    public void addObjectsForSale(String key, List<String> objects) {
        ForSaleConfig forSaleConfig = retrieve(key).forSaleConfig;
        if (forSaleConfig != null && !forSaleConfig.forSale) {
            throw new SeatsioException("Cannot add objects to the list of objects that are for sale when there are objects or categories marked as not for sale");
        }
        Set<String> oldObjectsForSale = forSaleConfig == null ? new LinkedHashSet<>() : toSetNullSafe(forSaleConfig.objects);
        List<String> categoriesForSale = forSaleConfig == null ? null : forSaleConfig.categories;
        markAsForSale(key, newArrayList(union(oldObjectsForSale, toSetNullSafe(objects))), categoriesForSale);
    }

    public void removeObjectsForSale(String key, List<String> objects) {
        ForSaleConfig forSaleConfig = retrieve(key).forSaleConfig;
        if (forSaleConfig != null && !forSaleConfig.forSale) {
            throw new SeatsioException("Cannot remove objects from the list of objects that are for sale when there are objects or categories marked as not for sale");
        }
        Set<String> oldObjectsForSale = forSaleConfig == null ? new LinkedHashSet<>() : toSetNullSafe(forSaleConfig.objects);
        Set<String> newObjectsForSale = difference(oldObjectsForSale, toSetNullSafe(objects));
        List<String> categoriesForSale = forSaleConfig == null ? null : forSaleConfig.categories;
        if (toSetNullSafe(categoriesForSale).isEmpty() && newObjectsForSale.isEmpty()) {
            markEverythingAsForSale(key);
        } else {
            markAsForSale(key, newArrayList(newObjectsForSale), categoriesForSale);
        }
    }

    public void addObjectsNotForSale(String key, List<String> objects) {
        ForSaleConfig forSaleConfig = retrieve(key).forSaleConfig;
        if (forSaleConfig != null && forSaleConfig.forSale) {
            throw new SeatsioException("Cannot add objects to the list of objects that are not for sale when there are objects or categories marked as for sale");
        }
        Set<String> oldObjectsNotForSale = forSaleConfig == null ? new LinkedHashSet<>() : toSetNullSafe(forSaleConfig.objects);
        List<String> categoriesNotForSale = forSaleConfig == null ? null : forSaleConfig.categories;
        markAsNotForSale(key, newArrayList(union(oldObjectsNotForSale, toSetNullSafe(objects))), categoriesNotForSale);
    }

    public void removeObjectsNotForSale(String key, List<String> objects) {
        ForSaleConfig forSaleConfig = retrieve(key).forSaleConfig;
        if (forSaleConfig != null && forSaleConfig.forSale) {
            throw new SeatsioException("Cannot remove objects from the list of objects that are not for sale when there are objects or categories marked as for sale");
        }
        Set<String> oldObjectsNotForSale = forSaleConfig == null ? new LinkedHashSet<>() : toSetNullSafe(forSaleConfig.objects);
        Set<String> newObjectsNotForSale = difference(oldObjectsNotForSale, toSetNullSafe(objects));
        List<String> categoriesNotForSale = forSaleConfig == null ? null : forSaleConfig.categories;
        if (toSetNullSafe(categoriesNotForSale).isEmpty() && newObjectsNotForSale.isEmpty()) {
            markEverythingAsForSale(key);
        } else {
            markAsNotForSale(key, newArrayList(newObjectsNotForSale), categoriesNotForSale);
        }
    }

    private static Set<String> toSetNullSafe(List<String> list) {
        if (list == null) {
            return newLinkedHashSet();
        }
        return newLinkedHashSet(list);
    }

    public void markAsNotForSale(String key, List<String> objects, List<String> categories) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/actions/mark-as-not-for-sale")
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString()));
    }

    private JsonObject forSaleRequest(List<String> objects, List<String> categories) {
        return aJsonObject()
                .withPropertyIfNotNull("objects", objects)
                .withPropertyIfNotNull("categories", categories)
                .build();
    }

    public void markEverythingAsForSale(String key) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/actions/mark-everything-as-for-sale")
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
                "/events/{key}/status-changes", ImmutableMap.of("key", eventKey), new HashMap<String, String>() {{
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
                "/events/{key}/objects/{objectId}/status-changes", ImmutableMap.of("key", key, "objectId", objectId),
                unirest,
                StatusChange.class);
        return new Lister<>(pageFetcher);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects) {
        return book(eventKey, objects, null, null, null, null, null, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken) {
        return book(eventKey, objects, holdToken, null, null, null, null, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        return changeObjectStatus(eventKey, objects, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing);
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

    public ChangeObjectStatusResult book(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        return changeObjectStatus(eventKeys, objects, BOOKED, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken) {
        return hold(eventKey, objects, holdToken, null, null, null, null, null);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        return changeObjectStatus(eventKey, objects, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken) {
        return hold(eventKey, bestAvailable, holdToken, null, null, null, null);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, bestAvailable, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys);
    }

    public ChangeObjectStatusResult hold(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        return changeObjectStatus(eventKeys, objects, HELD, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects) {
        return release(eventKey, objects, null, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken) {
        return release(eventKey, objects, holdToken, null, null, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKey, objects, FREE, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null);
    }

    public ChangeObjectStatusResult release(List<String> eventKeys, List<?> objects, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        return changeObjectStatus(eventKeys, objects, FREE, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status) {
        return changeObjectStatus(eventKey, bestAvailable, status, null, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken) {
        return changeObjectStatus(eventKey, bestAvailable, status, holdToken, null, null, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        String result = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/actions/change-object-status")
                .routeParam("key", eventKey)
                .body(changeObjectStatusRequest(bestAvailable, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys).toString()));
        return gson().fromJson(result, BestAvailableResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status) {
        return changeObjectStatus(eventKey, objects, status, null, null, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken) {
        return changeObjectStatus(eventKey, objects, status, holdToken, null, null, null, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing, allowedPreviousStatuses, rejectedPreviousStatuses);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing, null, null).toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/groups/actions/change-object-status")
                .queryString("expand", "objects")
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing, allowedPreviousStatuses, rejectedPreviousStatuses).toString()));
        return gson().fromJson(response, ChangeObjectStatusResult.class);
    }

    public List<ChangeObjectStatusResult> changeObjectStatus(List<StatusChangeRequest> statusChangeRequests) {
        List<JsonElement> statusChangeRequestsAsJson = statusChangeRequests
                .stream()
                .map(s -> changeObjectStatusRequest(s.eventKey, toObjects(s.objects), s.status, s.holdToken, s.orderId, s.keepExtraData, s.ignoreChannels, s.channelKeys, null, s.allowedPreviousStatuses, s.rejectedPreviousStatuses))
                .collect(toList());
        JsonObject request = aJsonObject()
                .withProperty("statusChanges", aJsonArray().withItems(statusChangeRequestsAsJson).build())
                .build();
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/actions/change-object-status")
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

    private JsonObject changeObjectStatusRequest(String eventKey, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing, allowedPreviousStatuses, rejectedPreviousStatuses);
        request.withProperty("event", eventKey);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(List<String> eventKeys, List<ObjectProperties> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, ignoreSocialDistancing, allowedPreviousStatuses, rejectedPreviousStatuses);
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(BestAvailable bestAvailable, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null, null);
        request.withProperty("bestAvailable", gson().toJsonTree(bestAvailable));
        return request.build();
    }

    private JsonObjectBuilder changeObjectStatusRequestBuilder(String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Boolean ignoreSocialDistancing, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        return aJsonObject()
                .withProperty("status", status)
                .withPropertyIfNotNull("holdToken", holdToken)
                .withPropertyIfNotNull("orderId", orderId)
                .withPropertyIfNotNull("keepExtraData", keepExtraData)
                .withPropertyIfNotNull("ignoreChannels", ignoreChannels)
                .withPropertyIfNotNull("channelKeys", channelKeys)
                .withPropertyIfNotNull("ignoreSocialDistancing", ignoreSocialDistancing)
                .withPropertyIfNotNull("allowedPreviousStatuses", allowedPreviousStatuses)
                .withPropertyIfNotNull("rejectedPreviousStatuses", rejectedPreviousStatuses);
    }

    public EventObjectInfo retrieveObjectInfo(String key, String label) {
        return retrieveObjectInfos(key, newArrayList(label)).get(label);
    }

    public Map<String, EventObjectInfo> retrieveObjectInfos(String key, List<String> labels) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/events/{key}/objects")
                .queryString("label", labels)
                .routeParam("key", key));
        TypeToken<Map<String, EventObjectInfo>> typeToken = new TypeToken<Map<String, EventObjectInfo>>() {
        };
        return gson().fromJson(response, typeToken.getType());
    }

    public void updateExtraData(String key, String object, Map<String, Object> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/objects/{object}/actions/update-extra-data")
                .routeParam("key", key)
                .routeParam("object", object)
                .body(request.build().toString()));
    }

    public void updateExtraDatas(String key, Map<String, Map<String, Object>> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/actions/update-extra-data")
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    private static class EventCreationResult {

        private List<Event> events;
    }
}

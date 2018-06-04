package seatsio.events;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.mashape.unirest.http.Unirest.get;
import static com.mashape.unirest.http.Unirest.post;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static seatsio.events.ObjectStatus.*;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Events {

    private final String secretKey;
    private final String baseUrl;

    public Events(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Event create(String chartKey) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("chartKey", chartKey);
        HttpResponse<String> response = stringResponse(post(baseUrl + "/events")
                .basicAuth(secretKey, null)
                .body(request.build().toString()));
        return gson().fromJson(response.getBody(), Event.class);
    }

    public void update(String key, String chartKey, String newKey, Boolean bookWholeTables) {
        JsonObjectBuilder request = aJsonObject();
        if (chartKey != null) {
            request.withProperty("chartKey", chartKey);
        }
        if (newKey != null) {
            request.withProperty("eventKey", newKey);
        }
        if (bookWholeTables != null) {
            request.withProperty("bookWholeTables", bookWholeTables);
        }
        stringResponse(post(baseUrl + "/events/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .body(request.build().toString()));
    }

    public Event retrieve(String key) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/events/{key}")
                .basicAuth(secretKey, null)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Event.class);
    }

    public void markAsForSale(String key, List<String> objects, List<String> categories) {
        stringResponse(post(baseUrl + "/events/{key}/actions/mark-as-for-sale")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString()));
    }

    public void markAsNotForSale(String key, List<String> objects, List<String> categories) {
        stringResponse(post(baseUrl + "/events/{key}/actions/mark-as-not-for-sale")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString()));
    }

    private JsonObject forSaleRequest(List<String> objects, List<String> categories) {
        JsonObjectBuilder request = aJsonObject();
        if (objects != null) {
            request.withProperty("objects", objects);
        }
        if (categories != null) {
            request.withProperty("categories", categories);
        }
        return request.build();
    }

    public void markEverythingAsForSale(String key) {
        stringResponse(post(baseUrl + "/events/{key}/actions/mark-everything-as-for-sale")
                .basicAuth(secretKey, null)
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
        return new Lister<>(new PageFetcher<>(baseUrl, "/events", secretKey, Event.class));
    }

    public Lister<StatusChange> statusChanges(String key) {
        PageFetcher<StatusChange> pageFetcher = new PageFetcher<>(
                baseUrl,
                "/events/{key}/status-changes",
                ImmutableMap.of("key", key),
                secretKey,
                StatusChange.class);
        return new Lister<>(pageFetcher);
    }

    public Lister<StatusChange> statusChanges(String key, String objectId) {
        PageFetcher<StatusChange> pageFetcher = new PageFetcher<>(
                baseUrl,
                "/events/{key}/objects/{objectId}/status-changes",
                ImmutableMap.of("key", key, "objectId", objectId),
                secretKey,
                StatusChange.class);
        return new Lister<>(pageFetcher);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects) {
        return book(eventKey, objects, null, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken) {
        return book(eventKey, objects, holdToken, null);
    }

    public ChangeObjectStatusResult book(String eventKey, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKey, objects, BOOKED, holdToken, orderId);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable) {
        return book(eventKey, bestAvailable, null, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable, String holdToken) {
        return book(eventKey, bestAvailable, holdToken, null);
    }

    public BestAvailableResult book(String eventKey, BestAvailable bestAvailable, String holdToken, String orderId) {
        return changeObjectStatus(eventKey, bestAvailable, BOOKED, holdToken, orderId);
    }

    public ChangeObjectStatusResult book(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKeys, objects, BOOKED, holdToken, orderId);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken) {
        return hold(eventKey, objects, holdToken, null);
    }

    public ChangeObjectStatusResult hold(String eventKey, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKey, objects, HELD, holdToken, orderId);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken) {
        return hold(eventKey, bestAvailable, holdToken, null);
    }

    public BestAvailableResult hold(String eventKey, BestAvailable bestAvailable, String holdToken, String orderId) {
        return changeObjectStatus(eventKey, bestAvailable, HELD, holdToken, orderId);
    }

    public ChangeObjectStatusResult hold(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKeys, objects, HELD, holdToken, orderId);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects) {
        return release(eventKey, objects, null, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken) {
        return release(eventKey, objects, holdToken, null);
    }

    public ChangeObjectStatusResult release(String eventKey, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKey, objects, FREE, holdToken, orderId);
    }

    public ChangeObjectStatusResult release(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        return changeObjectStatus(eventKeys, objects, FREE, holdToken, orderId);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status) {
        return changeObjectStatus(eventKey, bestAvailable, status, null, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken) {
        return changeObjectStatus(eventKey, bestAvailable, status, holdToken, null);
    }

    public BestAvailableResult changeObjectStatus(String eventKey, BestAvailable bestAvailable, String status, String holdToken, String orderId) {
        HttpResponse<String> result = stringResponse(post(baseUrl + "/events/{key}/actions/change-object-status")
                .routeParam("key", eventKey)
                .basicAuth(secretKey, null)
                .body(changeObjectStatusRequest(bestAvailable, status, holdToken, orderId).toString()));
        return gson().fromJson(result.getBody(), BestAvailableResult.class);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status) {
        return changeObjectStatus(eventKey, objects, status, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken) {
        return changeObjectStatus(eventKey, objects, status, holdToken, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId) {
        return changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status) {
        return changeObjectStatus(eventKeys, objects, status, null, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken) {
        return changeObjectStatus(eventKeys, objects, status, holdToken, null);
    }

    public ChangeObjectStatusResult changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/seasons/actions/change-object-status")
                .basicAuth(secretKey, null)
                .queryString("expand", "labels")
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId).toString()));
        return gson().fromJson(response.getBody(), ChangeObjectStatusResult.class);
    }

    private List<ObjectProperties> toObjects(List<?> objects) {
        return objects.stream()
                .map(ObjectProperties::from)
                .collect(toList());
    }

    private JsonObject changeObjectStatusRequest(List<String> eventKeys, List<ObjectProperties> objects, String status, String holdToken, String orderId) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId);
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        return request.build();
    }

    private JsonObject changeObjectStatusRequest(BestAvailable bestAvailable, String status, String holdToken, String orderId) {
        JsonObjectBuilder request = changeObjectStatusRequestBuilder(status, holdToken, orderId);
        request.withProperty("bestAvailable", gson().toJsonTree(bestAvailable));
        return request.build();
    }

    private JsonObjectBuilder changeObjectStatusRequestBuilder(String status, String holdToken, String orderId) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("status", status);
        if (holdToken != null) {
            request.withProperty("holdToken", holdToken);
        }
        if (orderId != null) {
            request.withProperty("orderId", orderId);
        }
        return request;
    }

    public ObjectStatus retrieveObjectStatus(String key, String object) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/events/{key}/objects/{object}")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .routeParam("object", object));
        return gson().fromJson(response.getBody(), ObjectStatus.class);
    }

    public void updateExtraData(String key, String object, Map<String, Object> extraData) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("extraData", gson().toJsonTree(extraData));
        stringResponse(post(baseUrl + "/events/{key}/objects/{object}/actions/update-extra-data")
                .routeParam("key", key)
                .routeParam("object", object)
                .basicAuth(secretKey, null)
                .body(request.build().toString()));
    }
}
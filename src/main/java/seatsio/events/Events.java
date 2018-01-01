package seatsio.events;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.Lister;
import seatsio.util.PageFetcher;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static seatsio.events.ObjectStatus.*;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.unirest;

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
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/events")
                .basicAuth(secretKey, null)
                .body(request.build().toString())
                .asString());
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
        unirest(() -> Unirest.post(baseUrl + "/events/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .body(request.build().toString())
                .asString());
    }

    public Event retrieve(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/events/{key}")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .asString());
        return gson().fromJson(response.getBody(), Event.class);
    }

    public void markAsForSale(String key, List<String> objects, List<String> categories) {
        unirest(() -> Unirest.post(baseUrl + "/events/{key}/actions/mark-as-for-sale")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString())
                .asString());
    }

    public void markAsNotForSale(String key, List<String> objects, List<String> categories) {
        unirest(() -> Unirest.post(baseUrl + "/events/{key}/actions/mark-as-not-for-sale")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .body(forSaleRequest(objects, categories).toString())
                .asString());
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
        unirest(() -> Unirest.post(baseUrl + "/events/{key}/actions/mark-everything-as-for-sale")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .asString());
    }

    public Lister<Event> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/events", secretKey, Event.class));
    }

    public void book(String eventKey, List<?> objects) {
        book(eventKey, objects, null, null);
    }

    public void book(String eventKey, List<?> objects, String holdToken) {
        book(eventKey, objects, holdToken, null);
    }

    public void book(String eventKey, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKey, objects, BOOKED, holdToken, orderId);
    }

    public void book(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKeys, objects, BOOKED, holdToken, orderId);
    }

    public void hold(String eventKey, List<?> objects, String holdToken) {
        hold(eventKey, objects, holdToken, null);
    }

    public void hold(String eventKey, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKey, objects, RESERVED_BY_TOKEN, holdToken, orderId);
    }

    public void hold(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKeys, objects, RESERVED_BY_TOKEN, holdToken, orderId);
    }

    public void release(String eventKey, List<?> objects) {
        release(eventKey, objects, null, null);
    }

    public void release(String eventKey, List<?> objects, String holdToken) {
        release(eventKey, objects, holdToken, null);
    }

    public void release(String eventKey, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKey, objects, FREE, holdToken, orderId);
    }

    public void release(List<String> eventKeys, List<?> objects, String holdToken, String orderId) {
        changeObjectStatus(eventKeys, objects, FREE, holdToken, orderId);
    }

    public void changeObjectStatus(String eventKey, List<?> objects, String status) {
        changeObjectStatus(eventKey, objects, status, null, null);
    }

    public void changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken) {
        changeObjectStatus(eventKey, objects, status, holdToken, null);
    }

    public void changeObjectStatus(String eventKey, List<?> objects, String status, String holdToken, String orderId) {
        changeObjectStatus(singletonList(eventKey), objects, status, holdToken, orderId);
    }

    public void changeObjectStatus(List<String> eventKeys, List<?> objects, String status, String holdToken, String orderId) {
        unirest(() -> Unirest.post(baseUrl + "/seasons/actions/change-object-status")
                .basicAuth(secretKey, null)
                .body(changeObjectStatusRequest(eventKeys, toObjects(objects), status, holdToken, orderId).toString())
                .asString());
    }

    private List<SeatsioObject> toObjects(List<?> objects) {
        return objects.stream()
                .map(SeatsioObject::from)
                .collect(toList());
    }

    private JsonObject changeObjectStatusRequest(List<String> eventKeys, List<SeatsioObject> objects, String status, String holdToken, String orderId) {
        JsonObjectBuilder request = aJsonObject();
        request.withProperty("events", eventKeys);
        request.withProperty("objects", objects, object -> gson().toJsonTree(object));
        request.withProperty("status", status);
        if (holdToken != null) {
            request.withProperty("holdToken", holdToken);
        }
        if (orderId != null) {
            request.withProperty("orderId", orderId);
        }
        return request.build();
    }

    public ObjectStatus getObjectStatus(String key, String object) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/events/{key}/objects/{object}")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .routeParam("object", object)
                .asString());
        return gson().fromJson(response.getBody(), ObjectStatus.class);
    }

}

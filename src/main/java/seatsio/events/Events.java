package seatsio.events;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import seatsio.json.JsonObjectBuilder;

import java.util.List;

import static seatsio.UnirestUtil.unirest;
import static seatsio.json.JsonObjectBuilder.aJsonObject;

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
        return new Gson().fromJson(response.getBody(), Event.class);
    }

    public Event retrieve(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/events/{key}")
                .basicAuth(secretKey, null)
                .routeParam("key", key)
                .asString());
        return new Gson().fromJson(response.getBody(), Event.class);
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
}

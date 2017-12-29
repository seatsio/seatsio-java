package seatsio.charts;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import seatsio.json.JsonObjectBuilder;

import java.util.List;
import java.util.Map;

import static seatsio.UnirestUtil.unirest;

public class Charts {

    private final String secretKey;
    private final String baseUrl;

    public Charts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Chart retrieve(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/charts/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart create() {
        return create(null);
    }

    public Chart create(String name) {
        return create(name, null);
    }

    public Chart create(String name, String venueType) {
        return create(name, venueType, null);
    }

    public Chart create(String name, String venueType, List<Category> categories) {
        JsonObjectBuilder request = JsonObjectBuilder.aJsonObject();
        if (name != null) {
            request.withProperty("name", name);
        }
        if (venueType != null) {
            request.withProperty("venueType", venueType);
        }
        if (categories != null) {
            request.withProperty("categories", categories, category -> new Gson().toJsonTree(category));
        }
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/charts")
                .basicAuth(secretKey, null)
                .body(request.build().toString())
                .asString());
        return new Gson().fromJson(response.getBody(), Chart.class);
    }

    public Map<?, ?> retrievePublishedVersion(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/charts/{key}/version/published")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Map.class);
    }

    public void update(String key, String name) {
        update(key, name, null);
    }

    public void update(String key, String name, List<Category> categories) {
        JsonObjectBuilder request = JsonObjectBuilder.aJsonObject();
        if (name != null) {
            request.withProperty("name", name);
        }
        if (categories != null) {
            request.withProperty("categories", categories, category -> new Gson().toJsonTree(category));
        }
        unirest(() -> Unirest.post(baseUrl + "/charts/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .body(request.build().toString())
                .asString());
    }

}

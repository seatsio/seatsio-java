package seatsio.charts;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import seatsio.Lister;
import seatsio.PageFetcher;
import seatsio.json.JsonObjectBuilder;

import java.io.InputStream;
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

    public InputStream retrievePublishedVersionThumbnail(String key) {
        HttpResponse<InputStream> response = unirest(() -> Unirest.get(baseUrl + "/charts/{key}/version/published/thumbnail")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asBinary());
        return response.getBody();
    }

    public Map<?, ?> retrieveDraftVersion(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/charts/{key}/version/draft")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Map.class);
    }

    public InputStream retrieveDraftVersionThumbnail(String key) {
        HttpResponse<InputStream> response = unirest(() -> Unirest.get(baseUrl + "/charts/{key}/version/draft/thumbnail")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asBinary());
        return response.getBody();
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

    public Chart copy(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/charts/{key}/version/published/actions/copy")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Chart.class);
    }

    public void moveToArchive(String key) {
        unirest(() -> Unirest.post(baseUrl + "/charts/archive/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
    }

    public void moveOutOfArchive(String key) {
        unirest(() -> Unirest.delete(baseUrl + "/charts/archive/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
    }

    public void publishDraftVersion(String key) {
        unirest(() -> Unirest.post(baseUrl + "/charts/{key}/version/draft/actions/publish")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
    }

    public void discardDraftVersion(String key) {
        unirest(() -> Unirest.post(baseUrl + "/charts/{key}/version/draft/actions/discard")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
    }

    public Chart copyDraftVersion(String key) {
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/charts/{key}/version/draft/actions/copy")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyToSubacccount(String key, long subaccountId) {
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/charts/{key}/version/published/actions/copy-to/{subaccountId}")
                .routeParam("key", key)
                .routeParam("subaccountId", Long.toString(subaccountId))
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Chart.class);
    }

    public void addTag(String key, String tag) {
        unirest(() -> Unirest.post(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag)
                .basicAuth(secretKey, null)
                .asString());
    }

    public void removeTag(String key, String tag) {
        unirest(() -> Unirest.delete(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag)
                .basicAuth(secretKey, null)
                .asString());
    }

    public List<String> listAllTags() {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/charts/tags")
                .basicAuth(secretKey, null)
                .asString());
        JsonElement tags = new JsonParser().parse(response.getBody()).getAsJsonObject().get("tags");
        return new Gson().fromJson(tags, List.class);
    }

    public Lister<Chart> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/charts", secretKey, Chart.class));
    }

    public Lister<Chart> archive() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/charts/archive", secretKey, Chart.class));
    }

}

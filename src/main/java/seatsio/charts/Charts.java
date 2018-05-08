package seatsio.charts;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;
import seatsio.util.ParameterizedLister;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.mashape.unirest.http.Unirest.*;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.binaryResponse;
import static seatsio.util.UnirestUtil.stringResponse;

public class Charts {

    private final String secretKey;
    private final String baseUrl;

    public final Lister<Chart> archive;

    public Charts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.archive = new Lister<>(new PageFetcher<>(baseUrl, "/charts/archive", secretKey, Chart.class));
    }

    public Chart retrieve(String key) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/charts/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart retrieveWithEvents(String key) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/charts/{key}?expand=events")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Chart.class);
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
            request.withProperty("categories", categories, category -> gson().toJsonTree(category));
        }
        HttpResponse<String> response = stringResponse(post(baseUrl + "/charts")
                .basicAuth(secretKey, null)
                .body(request.build().toString()));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Map<?, ?> retrievePublishedVersion(String key) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/charts/{key}/version/published")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Map.class);
    }

    public InputStream retrievePublishedVersionThumbnail(String key) {
        HttpResponse<InputStream> response = binaryResponse(get(baseUrl + "/charts/{key}/version/published/thumbnail")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return response.getBody();
    }

    public Map<?, ?> retrieveDraftVersion(String key) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/charts/{key}/version/draft")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Map.class);
    }

    public InputStream retrieveDraftVersionThumbnail(String key) {
        HttpResponse<InputStream> response = binaryResponse(get(baseUrl + "/charts/{key}/version/draft/thumbnail")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
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
            request.withProperty("categories", categories, category -> gson().toJsonTree(category));
        }
        stringResponse(post(baseUrl + "/charts/{key}")
                .routeParam("key", key)
                .basicAuth(secretKey, null)
                .body(request.build().toString()));
    }

    public Chart copy(String key) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/charts/{key}/version/published/actions/copy")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public void moveToArchive(String key) {
        stringResponse(post(baseUrl + "/charts/{key}/actions/move-to-archive")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
    }

    public void moveOutOfArchive(String key) {
        stringResponse(post(baseUrl + "/charts/{key}/actions/move-out-of-archive")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
    }

    public void publishDraftVersion(String key) {
        stringResponse(post(baseUrl + "/charts/{key}/version/draft/actions/publish")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
    }

    public void discardDraftVersion(String key) {
        stringResponse(post(baseUrl + "/charts/{key}/version/draft/actions/discard")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
    }

    public Chart copyDraftVersion(String key) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/charts/{key}/version/draft/actions/copy")
                .routeParam("key", key)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyToSubacccount(String key, long subaccountId) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/charts/{key}/version/published/actions/copy-to/{subaccountId}")
                .routeParam("key", key)
                .routeParam("subaccountId", Long.toString(subaccountId))
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public void addTag(String key, String tag) {
        stringResponse(post(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag)
                .basicAuth(secretKey, null));
    }

    public void removeTag(String key, String tag) {
        stringResponse(delete(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag)
                .basicAuth(secretKey, null));
    }

    public List<String> listAllTags() {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/charts/tags")
                .basicAuth(secretKey, null));
        JsonElement tags = new JsonParser().parse(response.getBody()).getAsJsonObject().get("tags");
        return gson().fromJson(tags, List.class);
    }

    public Stream<Chart> listAll() {
        return listAll(new ChartListParams());
    }

    public Stream<Chart> listAll(ChartListParams chartListParams) {
        return list().all(chartListParams.asMap());
    }

    public Page<Chart> listFirstPage() {
        return listFirstPage(new ChartListParams(), null);
    }

    public Page<Chart> listFirstPage(ChartListParams chartListParams, Integer pageSize) {
        return list().firstPage(toMap(chartListParams), pageSize);
    }

    public Page<Chart> listPageAfter(long id) {
        return listPageAfter(id, new ChartListParams(), null);
    }

    public Page<Chart> listPageAfter(long id, ChartListParams chartListParams, Integer pageSize) {
        return list().pageAfter(id, toMap(chartListParams), pageSize);
    }

    public Page<Chart> listPageBefore(long id) {
        return listPageBefore(id, new ChartListParams(), null);
    }

    public Page<Chart> listPageBefore(long id, ChartListParams chartListParams, Integer pageSize) {
        return list().pageBefore(id, toMap(chartListParams), pageSize);
    }

    private ParameterizedLister<Chart> list() {
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/charts", secretKey, Chart.class));
    }

    private Map<String, Object> toMap(ChartListParams chartListParams) {
        if (chartListParams == null) {
            return new HashMap<>();
        }
        return chartListParams.asMap();
    }
}

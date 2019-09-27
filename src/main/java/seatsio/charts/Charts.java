package seatsio.charts;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.binaryResponse;
import static seatsio.util.UnirestUtil.stringResponse;

public class Charts {

    private final String secretKey;
    private final Long accountId;
    private final String baseUrl;

    public final Lister<Chart> archive;

    public Charts(String secretKey, Long accountId, String baseUrl) {
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.baseUrl = baseUrl;
        this.archive = new Lister<>(new PageFetcher<>(baseUrl, "/charts/archive", secretKey, accountId, Chart.class));
    }

    public Chart retrieve(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart retrieveWithEvents(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}?expand=events", secretKey, accountId)
                .routeParam("key", key));
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
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("venueType", venueType)
                .withPropertyIfNotNull("categories", categories, category -> gson().toJsonTree(category));
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts", secretKey, accountId)
                .body(request.build().toString()));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Map<?, ?> retrievePublishedVersion(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/published", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Map.class);
    }

    public InputStream retrievePublishedVersionThumbnail(String key) {
        HttpResponse<InputStream> response = binaryResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/published/thumbnail", secretKey, accountId)
                .routeParam("key", key));
        return response.getBody();
    }

    public Map<?, ?> retrieveDraftVersion(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/draft", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Map.class);
    }

    public InputStream retrieveDraftVersionThumbnail(String key) {
        HttpResponse<InputStream> response = binaryResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/draft/thumbnail", secretKey, accountId)
                .routeParam("key", key));
        return response.getBody();
    }

    public void update(String key, String name) {
        update(key, name, null);
    }

    public void update(String key, String name, List<Category> categories) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("categories", categories, category -> gson().toJsonTree(category));
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}", secretKey, accountId)
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    public Chart copy(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/published/actions/copy", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public void moveToArchive(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/actions/move-to-archive", secretKey, accountId)
                .routeParam("key", key));
    }

    public void moveOutOfArchive(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/actions/move-out-of-archive", secretKey, accountId)
                .routeParam("key", key));
    }

    public void publishDraftVersion(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/publish", secretKey, accountId)
                .routeParam("key", key));
    }

    public void discardDraftVersion(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/discard", secretKey, accountId)
                .routeParam("key", key));
    }

    public Chart copyDraftVersion(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/copy", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyToSubacccount(String key, long subaccountId) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/published/actions/copy-to/{subaccountId}", secretKey, accountId)
                .routeParam("key", key)
                .routeParam("subaccountId", Long.toString(subaccountId)));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public void addTag(String key, String tag) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/tags/{tag}", secretKey, accountId)
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public void removeTag(String key, String tag) {
        stringResponse(UnirestUtil.delete(baseUrl + "/charts/{key}/tags/{tag}", secretKey, accountId)
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public ChartValidationResult validatePublishedVersion(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/published/actions/validate", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), ChartValidationResult.class);
    }

    public ChartValidationResult validateDraftVersion(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/validate", secretKey, accountId)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), ChartValidationResult.class);
    }

    public List<String> listAllTags() {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/charts/tags", secretKey, accountId));
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
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/charts", secretKey, accountId, Chart.class));
    }

    private Map<String, Object> toMap(ChartListParams chartListParams) {
        if (chartListParams == null) {
            return new HashMap<>();
        }
        return chartListParams.asMap();
    }
}

package seatsio.charts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

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
    private final String workspaceKey;
    private final String baseUrl;

    public final Lister<Chart> archive;

    public Charts(String secretKey, String workspaceKey, String baseUrl) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
        this.archive = new Lister<>(new PageFetcher<>(baseUrl, "/charts/archive", secretKey, workspaceKey, Chart.class));
    }

    public Chart retrieve(String key) {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public Chart retrieveWithEvents(String key) {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}?expand=events", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
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
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts", secretKey, workspaceKey)
                .body(request.build().toString()));
        return gson().fromJson(response, Chart.class);
    }

    public Map<?, ?> retrievePublishedVersion(String key) {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/published", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Map.class);
    }

    public byte[] retrievePublishedVersionThumbnail(String key) {
        return binaryResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/published/thumbnail", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public Map<?, ?> retrieveDraftVersion(String key) {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/draft", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Map.class);
    }

    public byte[] retrieveDraftVersionThumbnail(String key) {
        return binaryResponse(UnirestUtil.get(baseUrl + "/charts/{key}/version/draft/thumbnail", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void update(String key, String name) {
        update(key, name, null);
    }

    public void update(String key, String name, List<Category> categories) {
        JsonObjectBuilder request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("categories", categories, category -> gson().toJsonTree(category));
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}", secretKey, workspaceKey)
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    public Chart copy(String key) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/published/actions/copy", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public void moveToArchive(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/actions/move-to-archive", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void moveOutOfArchive(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/actions/move-out-of-archive", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void publishDraftVersion(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/publish", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void discardDraftVersion(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/discard", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public Chart copyDraftVersion(String key) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/copy", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public Chart copyToSubacccount(String chartKey, long subaccountId) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{chartKey}/version/published/actions/copy-to/{subaccountId}", secretKey, workspaceKey)
                .routeParam("chartKey", chartKey)
                .routeParam("subaccountId", Long.toString(subaccountId)));
        return gson().fromJson(response, Chart.class);
    }

    public Chart copyToWorkspace(String chartKey, String toWorkspaceKey) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{chartKey}/version/published/actions/copy-to-workspace/{workspaceKey}", secretKey, workspaceKey)
                .routeParam("chartKey", chartKey)
                .routeParam("workspaceKey", toWorkspaceKey));
        return gson().fromJson(response, Chart.class);
    }

    public void addTag(String key, String tag) {
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/tags/{tag}", secretKey, workspaceKey)
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public void removeTag(String key, String tag) {
        stringResponse(UnirestUtil.delete(baseUrl + "/charts/{key}/tags/{tag}", secretKey, workspaceKey)
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public void saveSocialDistancingRulesets(String key, Map<String, SocialDistancingRuleset> rulesets) {
        JsonObject request = aJsonObject()
                .withProperty("socialDistancingRulesets", gson().toJsonTree(rulesets))
                .build();
        stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/social-distancing-rulesets", secretKey, workspaceKey)
                .routeParam("key", key)
                .body(request.toString()));
    }

    public ChartValidationResult validatePublishedVersion(String key) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/published/actions/validate", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, ChartValidationResult.class);
    }

    public ChartValidationResult validateDraftVersion(String key) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/charts/{key}/version/draft/actions/validate", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, ChartValidationResult.class);
    }

    public List<String> listAllTags() {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/charts/tags", secretKey, workspaceKey));
        JsonElement tags = JsonParser.parseString(response).getAsJsonObject().get("tags");
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
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/charts", secretKey, workspaceKey, Chart.class));
    }

    private Map<String, Object> toMap(ChartListParams chartListParams) {
        if (chartListParams == null) {
            return new HashMap<>();
        }
        return chartListParams.asMap();
    }

}

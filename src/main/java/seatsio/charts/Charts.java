package seatsio.charts;

import com.google.gson.*;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

import java.util.*;
import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.get;

public class Charts {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public final Lister<Chart> archive;

    public Charts(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.archive = new Lister<>(new PageFetcher<>(baseUrl, "/charts/archive", unirest, Chart.class));
        this.unirest = unirest;
    }

    public Chart retrieve(String key) {
        String response = unirest.stringResponse(get(baseUrl + "/charts/{key}")
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public Chart retrieveWithEvents(String key) {
        String response = unirest.stringResponse(get(baseUrl + "/charts/{key}?expand=events")
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
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts")
                .body(request.build().toString()));
        return gson().fromJson(response, Chart.class);
    }

    public Map<?, ?> retrievePublishedVersion(String key) {
        String response = unirest.stringResponse(get(baseUrl + "/charts/{key}/version/published")
                .routeParam("key", key));
        return gson().fromJson(response, Map.class);
    }

    public byte[] retrievePublishedVersionThumbnail(String key) {
        return unirest.binaryResponse(get(baseUrl + "/charts/{key}/version/published/thumbnail")
                .routeParam("key", key));
    }

    public Map<?, ?> retrieveDraftVersion(String key) {
        String response = unirest.stringResponse(get(baseUrl + "/charts/{key}/version/draft")
                .routeParam("key", key));
        return gson().fromJson(response, Map.class);
    }

    public byte[] retrieveDraftVersionThumbnail(String key) {
        return unirest.binaryResponse(get(baseUrl + "/charts/{key}/version/draft/thumbnail")
                .routeParam("key", key));
    }

    public void update(String key, String name) {
        update(key, name, null);
    }

    public void update(String key, String name, List<Category> categories) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}")
                .routeParam("key", key)
                .body(aJsonObject()
                        .withPropertyIfNotNull("name", name)
                        .withPropertyIfNotNull("categories", categories, category -> gson().toJsonTree(category))
                        .buildAsString()
                ));
    }


    public void addCategory(String chartKey, Category category) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/categories")
                .routeParam("key", chartKey)
                .body(gson().toJsonTree(category).toString()));
    }

    public void removeCategory(String chartKey, CategoryKey categoryKey) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/charts/{chartKey}/categories/{categoryKey}")
                .routeParam("chartKey", chartKey)
                .routeParam("categoryKey", categoryKey.toString()));
    }

    public List<Category> listCategories(String chartKey) {
        final String response = unirest.stringResponse(get(baseUrl + "/charts/{key}/categories")
                .routeParam("key", chartKey));
        final Gson gson = gson();
        return gson.fromJson(response, ListCategoriesResponse.class).categories;
    }

    public Chart copy(String key) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/published/actions/copy")
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public void moveToArchive(String key) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/actions/move-to-archive")
                .routeParam("key", key));
    }

    public void moveOutOfArchive(String key) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/actions/move-out-of-archive")
                .routeParam("key", key));
    }

    public void publishDraftVersion(String key) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/draft/actions/publish")
                .routeParam("key", key));
    }

    public void discardDraftVersion(String key) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/draft/actions/discard")
                .routeParam("key", key));
    }

    public Chart copyDraftVersion(String key) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/draft/actions/copy")
                .routeParam("key", key));
        return gson().fromJson(response, Chart.class);
    }

    public Chart copyToWorkspace(String chartKey, String toWorkspaceKey) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{chartKey}/version/published/actions/copy-to-workspace/{workspaceKey}")
                .routeParam("chartKey", chartKey)
                .routeParam("workspaceKey", toWorkspaceKey));
        return gson().fromJson(response, Chart.class);
    }

    public Chart copyToWorkspace(String chartKey, String fromWorkspaceKey, String toWorkspaceKey) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{chartKey}/version/published/actions/copy/from/{fromWorkspaceKey}/to/{toWorkspaceKey}")
                .routeParam("chartKey", chartKey)
                .routeParam("fromWorkspaceKey", fromWorkspaceKey)
                .routeParam("toWorkspaceKey", toWorkspaceKey));
        return gson().fromJson(response, Chart.class);
    }

    public void addTag(String key, String tag) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public void removeTag(String key, String tag) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/charts/{key}/tags/{tag}")
                .routeParam("key", key)
                .routeParam("tag", tag));
    }

    public ChartValidationResult validatePublishedVersion(String key) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/published/actions/validate")
                .routeParam("key", key));
        return gson().fromJson(response, ChartValidationResult.class);
    }

    public ChartValidationResult validateDraftVersion(String key) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/charts/{key}/version/draft/actions/validate")
                .routeParam("key", key));
        return gson().fromJson(response, ChartValidationResult.class);
    }

    public List<String> listAllTags() {
        String response = unirest.stringResponse(get(baseUrl + "/charts/tags"));
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
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/charts", unirest, Chart.class));
    }

    private Map<String, Object> toMap(ChartListParams chartListParams) {
        if (chartListParams == null) {
            return new HashMap<>();
        }
        return chartListParams.asMap();
    }

    private static final class ListCategoriesResponse {
        public List<Category> categories;
    }
}

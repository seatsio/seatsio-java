package seatsio.workspaces;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Workspaces {

    private final String secretKey;
    private final String workspaceKey;
    private final String baseUrl;

    public final WorkspaceLister<Workspace> active;
    public final WorkspaceLister<Workspace> inactive;

    public Workspaces(String secretKey, String workspaceKey, String baseUrl) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
        this.active = new WorkspaceLister<>(new PageFetcher<>(baseUrl, "/workspaces/active", secretKey, workspaceKey, Workspace.class));
        this.inactive = new WorkspaceLister<>(new PageFetcher<>(baseUrl, "/workspaces/inactive", secretKey, workspaceKey, Workspace.class));
    }

    public Workspace create(String name) {
        return create(name, false);
    }

    public Workspace create(String name, Boolean isTest) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("name", name)
                .withPropertyIfNotNull("isTest", isTest);

        String response = stringResponse(UnirestUtil.post(baseUrl + "/workspaces", secretKey, workspaceKey)
                .body(request.build().toString()));

        return gson().fromJson(response, Workspace.class);
    }

    public void update(String key, String name) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("name", name);

        stringResponse(UnirestUtil.post(baseUrl + "/workspaces/{key}", secretKey, workspaceKey)
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    public String regenerateSecretKey(String key) {
        String response = stringResponse(UnirestUtil.post(baseUrl + "/workspaces/{key}/actions/regenerate-secret-key", secretKey, workspaceKey)
                .routeParam("key", key));

        JsonObject result = JsonParser.parseString(response)
                .getAsJsonObject();
        return result.getAsJsonPrimitive("secretKey").getAsString();
    }

    public void activate(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/workspaces/{key}/actions/activate", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void deactivate(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/workspaces/{key}/actions/deactivate", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public void setDefault(String key) {
        stringResponse(UnirestUtil.post(baseUrl + "/workspaces/actions/set-default/{key}", secretKey, workspaceKey)
                .routeParam("key", key));
    }

    public Workspace retrieve(String key) {
        String response = stringResponse(UnirestUtil.get(baseUrl + "/workspaces/{key}", secretKey, workspaceKey)
                .routeParam("key", key));
        return gson().fromJson(response, Workspace.class);
    }

    public Stream<Workspace> listAll() {
        return list().all();
    }

    public Stream<Workspace> listAll(String filter) {
        return parametrizedList().all(toMap(filter));
    }

    public Page<Workspace> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Workspace> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Workspace> listFirstPage(Integer pageSize, String filter) {
        return parametrizedList().firstPage(toMap(filter), pageSize);
    }

    public Page<Workspace> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Workspace> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Workspace> listPageAfter(long id, Integer pageSize, String filter) {
        return parametrizedList().pageAfter(id, toMap(filter), pageSize);
    }

    public Page<Workspace> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Workspace> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    public Page<Workspace> listPageBefore(long id, Integer pageSize, String filter) {
        return parametrizedList().pageBefore(id, toMap(filter), pageSize);
    }

    private Lister<Workspace> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/workspaces", secretKey, Workspace.class));
    }

    private ParameterizedLister<Workspace> parametrizedList() {
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/workspaces", secretKey, Workspace.class));
    }

    protected static Map<String, Object> toMap(String filter) {
        HashMap<String, Object> map = new HashMap<>();
        if (filter != null) {
            map.put("filter", filter);
        }
        return map;
    }
}

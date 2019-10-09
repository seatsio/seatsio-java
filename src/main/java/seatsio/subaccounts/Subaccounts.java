package seatsio.subaccounts;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import seatsio.charts.Chart;
import seatsio.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Subaccounts {

    private final String secretKey;
    private final String workspaceKey;
    private final String baseUrl;

    public final Lister<Subaccount> active;
    public final Lister<Subaccount> inactive;

    public Subaccounts(String secretKey, String workspaceKey, String baseUrl) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
        this.active = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/active", secretKey, workspaceKey, Subaccount.class));
        this.inactive = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/inactive", secretKey, workspaceKey, Subaccount.class));
    }

    public Subaccount create(String name) {
        return doCreate(null, name);
    }

    public Subaccount createWithEmail(String email) {
        return doCreate(email, null);
    }

    public Subaccount createWithEmail(String email, String name) {
        return doCreate(email, name);
    }

    public Subaccount doCreate(String email, String name) {
        JsonObject request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("email", email)
                .build();
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/subaccounts", secretKey, workspaceKey)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void update(long id, String name) {
        update(id, name, null);
    }

    public void update(long id, String name, String email) {
        JsonObject request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .withPropertyIfNotNull("email", email)
                .build();
        stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id))
                .body(request.toString()));
    }

    public Subaccount create() {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/subaccounts", secretKey, workspaceKey));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public Subaccount retrieve(long id) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/subaccounts/{id}", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id)));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void activate(long id) {
        stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}/actions/activate", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id)));
    }

    public void deactivate(long id) {
        stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}/actions/deactivate", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id)));
    }

    public void regenerateSecretKey(long id) {
        stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}/secret-key/actions/regenerate", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id)));
    }

    public void regenerateDesignerKey(long id) {
        stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}/designer-key/actions/regenerate", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id)));
    }

    public Chart copyChartToParent(long id, String chartKey) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{id}/charts/{chartKey}/actions/copy-to/parent", secretKey, workspaceKey)
                .routeParam("id", Long.toString(id))
                .routeParam("chartKey", chartKey));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyChartToSubaccount(long fromId, long toId, String chartKey) {
        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/subaccounts/{fromId}/charts/{chartKey}/actions/copy-to/{toId}", secretKey, workspaceKey)
                .routeParam("fromId", Long.toString(fromId))
                .routeParam("chartKey", chartKey)
                .routeParam("toId", Long.toString(toId)));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Stream<Subaccount> listAll() {
        return list().all();
    }

    public Stream<Subaccount> listAll(String filter) {
        return parametrizedList().all(toMap(filter));
    }

    public Page<Subaccount> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Subaccount> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Subaccount> listFirstPage(Integer pageSize, String filter) {
        return parametrizedList().firstPage(toMap(filter), pageSize);
    }

    public Page<Subaccount> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Subaccount> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Subaccount> listPageAfter(long id, Integer pageSize, String filter) {
        return parametrizedList().pageAfter(id, toMap(filter), pageSize);
    }

    public Page<Subaccount> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Subaccount> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    public Page<Subaccount> listPageBefore(long id, Integer pageSize, String filter) {
        return parametrizedList().pageBefore(id, toMap(filter), pageSize);
    }

    private Lister<Subaccount> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, workspaceKey, Subaccount.class));
    }

    private ParameterizedLister<Subaccount> parametrizedList() {
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, workspaceKey, Subaccount.class));
    }

    private Map<String, Object> toMap(String filter) {
        HashMap<String, Object> map = new HashMap<>();
        if (filter != null) {
            map.put("filter", filter);
        }
        return map;
    }

}

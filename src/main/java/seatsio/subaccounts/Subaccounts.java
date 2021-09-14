package seatsio.subaccounts;

import com.google.gson.JsonObject;
import seatsio.charts.Chart;
import seatsio.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;

public class Subaccounts {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public final Lister<Subaccount> active;
    public final Lister<Subaccount> inactive;

    public Subaccounts(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.active = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/active", unirest, Subaccount.class));
        this.inactive = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/inactive", unirest, Subaccount.class));
        this.unirest = unirest;
    }

    public Subaccount create(String name) {
        JsonObject request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .build();
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts")
                .body(request.toString()));
        return gson().fromJson(response, Subaccount.class);
    }

    public void update(long id, String name) {
        JsonObject request = aJsonObject()
                .withPropertyIfNotNull("name", name)
                .build();
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id))
                .body(request.toString()));
    }

    public Subaccount create() {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts"));
        return gson().fromJson(response, Subaccount.class);
    }

    public Subaccount retrieve(long id) {
        String response = unirest.stringResponse(UnirestWrapper.get(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id)));
        return gson().fromJson(response, Subaccount.class);
    }

    public void activate(long id) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}/actions/activate")
                .routeParam("id", Long.toString(id)));
    }

    public void deactivate(long id) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}/actions/deactivate")
                .routeParam("id", Long.toString(id)));
    }

    public void regenerateSecretKey(long id) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}/secret-key/actions/regenerate")
                .routeParam("id", Long.toString(id)));
    }

    public void regenerateDesignerKey(long id) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}/designer-key/actions/regenerate")
                .routeParam("id", Long.toString(id)));
    }

    public Chart copyChartToParent(long id, String chartKey) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{id}/charts/{chartKey}/actions/copy-to/parent")
                .routeParam("id", Long.toString(id))
                .routeParam("chartKey", chartKey));
        return gson().fromJson(response, Chart.class);
    }

    public Chart copyChartToSubaccount(long fromId, long toId, String chartKey) {
        String response = unirest.stringResponse(UnirestWrapper.post(baseUrl + "/subaccounts/{fromId}/charts/{chartKey}/actions/copy-to/{toId}")
                .routeParam("fromId", Long.toString(fromId))
                .routeParam("chartKey", chartKey)
                .routeParam("toId", Long.toString(toId)));
        return gson().fromJson(response, Chart.class);
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
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", unirest, Subaccount.class));
    }

    private ParameterizedLister<Subaccount> parametrizedList() {
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/subaccounts", unirest, Subaccount.class));
    }

    private Map<String, Object> toMap(String filter) {
        HashMap<String, Object> map = new HashMap<>();
        if (filter != null) {
            map.put("filter", filter);
        }
        return map;
    }

}

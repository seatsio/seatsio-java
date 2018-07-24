package seatsio.subaccounts;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import seatsio.charts.Chart;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;

import java.util.stream.Stream;

import static com.mashape.unirest.http.Unirest.get;
import static com.mashape.unirest.http.Unirest.post;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Subaccounts {

    private final String secretKey;
    private final String baseUrl;

    public final Lister<Subaccount> active;
    public final Lister<Subaccount> inactive;

    public Subaccounts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.active = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/active", secretKey, Subaccount.class));
        this.inactive = new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts/inactive", secretKey, Subaccount.class));
    }

    public Subaccount create(String name) {
        JsonObject request = aJsonObject().withProperty("name", name).build();
        HttpResponse<String> response = stringResponse(post(baseUrl + "/subaccounts")
                .basicAuth(secretKey, null)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public Subaccount createWithEmail(String email) {
        JsonObject request = aJsonObject().withProperty("email", email).build();
        HttpResponse<String> response = stringResponse(post(baseUrl + "/subaccounts")
                .basicAuth(secretKey, null)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void update(long id, String name) {
        JsonObject request = aJsonObject().withProperty("name", name).build();
        stringResponse(post(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .body(request.toString()));
    }

    public Subaccount create() {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/subaccounts")
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public Subaccount retrieve(long id) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void activate(long id) {
        stringResponse(post(baseUrl + "/subaccounts/{id}/actions/activate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null));
    }

    public void deactivate(long id) {
        stringResponse(post(baseUrl + "/subaccounts/{id}/actions/deactivate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null));
    }

    public void regenerateSecretKey(long id) {
        stringResponse(post(baseUrl + "/subaccounts/{id}/secret-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null));
    }

    public void regenerateDesignerKey(long id) {
        stringResponse(post(baseUrl + "/subaccounts/{id}/designer-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null));
    }

    public Chart copyChartToParent(long id, String chartKey) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/subaccounts/{id}/charts/{chartKey}/actions/copy-to/parent")
                .basicAuth(secretKey, null)
                .routeParam("id", Long.toString(id))
                .routeParam("chartKey", chartKey));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyChartToSubaccount(long fromId, long toId, String chartKey) {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/subaccounts/{fromId}/charts/{chartKey}/actions/copy-to/{toId}")
                .basicAuth(secretKey, null)
                .routeParam("fromId", Long.toString(fromId))
                .routeParam("chartKey", chartKey)
                .routeParam("toId", Long.toString(toId)));
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Stream<Subaccount> listAll() {
        return list().all();
    }

    public Page<Subaccount> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Subaccount> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Subaccount> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Subaccount> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Subaccount> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Subaccount> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    private Lister<Subaccount> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, Subaccount.class));
    }

}

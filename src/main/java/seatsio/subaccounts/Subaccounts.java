package seatsio.subaccounts;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import seatsio.charts.Chart;
import seatsio.util.Lister;
import seatsio.util.PageFetcher;

import static com.mashape.unirest.http.Unirest.get;
import static com.mashape.unirest.http.Unirest.post;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Subaccounts {

    private final String secretKey;
    private final String baseUrl;

    public Subaccounts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Subaccount create(String name) {
        JsonObject request = aJsonObject().withProperty("name", name).build();
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

    public Lister<Subaccount> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, Subaccount.class));
    }

}

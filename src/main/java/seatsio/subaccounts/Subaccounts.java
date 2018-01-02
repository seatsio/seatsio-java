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
import static seatsio.util.UnirestUtil.unirest;

public class Subaccounts {

    private final String secretKey;
    private final String baseUrl;

    public Subaccounts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Subaccount create(String name) {
        HttpResponse<String> response = unirest(() -> {
            JsonObject request = aJsonObject().withProperty("name", name).build();
            return post(baseUrl + "/subaccounts")
                    .basicAuth(secretKey, null)
                    .body(request.toString())
                    .asString();
        });
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void update(long id, String name) {
        unirest(() -> {
            JsonObject request = aJsonObject().withProperty("name", name).build();
            return post(baseUrl + "/subaccounts/{id}")
                    .routeParam("id", Long.toString(id))
                    .basicAuth(secretKey, null)
                    .body(request.toString())
                    .asString();
        });
    }

    public Subaccount create() {
        HttpResponse<String> response = unirest(() -> post(baseUrl + "/subaccounts")
                .basicAuth(secretKey, null)
                .asString());
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public Subaccount retrieve(long id) {
        HttpResponse<String> response = unirest(() -> get(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asString());
        return gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void activate(long id) {
        unirest(() -> post(baseUrl + "/subaccounts/{id}/actions/activate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void deactivate(long id) {
        unirest(() -> post(baseUrl + "/subaccounts/{id}/actions/deactivate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void regenerateSecretKey(long id) {
        unirest(() -> post(baseUrl + "/subaccounts/{id}/secret-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void regenerateDesignerKey(long id) {
        unirest(() -> post(baseUrl + "/subaccounts/{id}/designer-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public Chart copyChartToParent(long id, String chartKey) {
        HttpResponse<String> response = unirest(() -> post(baseUrl + "/subaccounts/{id}/charts/{chartKey}/actions/copy-to/parent")
                .basicAuth(secretKey, null)
                .routeParam("id", Long.toString(id))
                .routeParam("chartKey", chartKey)
                .asString());
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Chart copyChartToSubaccount(long fromId, long toId, String chartKey) {
        HttpResponse<String> response = unirest(() -> post(baseUrl + "/subaccounts/{fromId}/charts/{chartKey}/actions/copy-to/{toId}")
                .basicAuth(secretKey, null)
                .routeParam("fromId", Long.toString(fromId))
                .routeParam("chartKey", chartKey)
                .routeParam("toId", Long.toString(toId))
                .asString());
        return gson().fromJson(response.getBody(), Chart.class);
    }

    public Lister<Subaccount> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, Subaccount.class));
    }

}

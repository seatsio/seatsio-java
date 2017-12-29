package seatsio.subaccounts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import seatsio.Lister;
import seatsio.PageFetcher;
import seatsio.UnirestExceptionThrowingSupplier;

import static seatsio.UnirestUtil.unirest;
import static seatsio.json.JsonObjectBuilder.aJsonObject;

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
            return Unirest.post(baseUrl + "/subaccounts")
                    .basicAuth(secretKey, null)
                    .body(request.toString())
                    .asString();
        });
        return new Gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void update(long id, String name) {
        unirest(() -> {
            JsonObject request = aJsonObject().withProperty("name", name).build();
            return Unirest.post(baseUrl + "/subaccounts/{id}")
                    .routeParam("id", Long.toString(id))
                    .basicAuth(secretKey, null)
                    .body(request.toString())
                    .asString();
        });
    }

    public Subaccount create() {
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/subaccounts")
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Subaccount.class);
    }

    public Subaccount retrieve(long id) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/subaccounts/{id}")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asString());
        return new Gson().fromJson(response.getBody(), Subaccount.class);
    }

    public void activate(long id) {
        unirest(() -> Unirest.post(baseUrl + "/subaccounts/{id}/actions/activate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void deactivate(long id) {
        unirest(() -> Unirest.post(baseUrl + "/subaccounts/{id}/actions/deactivate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void regenerateSecretKey(long id) {
        unirest(() -> Unirest.post(baseUrl + "/subaccounts/{id}/secret-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public void regenerateDesignerKey(long id) {
        unirest(() -> Unirest.post(baseUrl + "/subaccounts/{id}/designer-key/actions/regenerate")
                .routeParam("id", Long.toString(id))
                .basicAuth(secretKey, null)
                .asBinary());
    }

    public Lister<Subaccount> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/subaccounts", secretKey, Subaccount.class));
    }

}

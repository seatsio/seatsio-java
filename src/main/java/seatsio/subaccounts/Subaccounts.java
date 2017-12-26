package seatsio.subaccounts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import seatsio.UnirestExceptionThrowingSupplier;

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

    private <T> HttpResponse<T> unirest(UnirestExceptionThrowingSupplier<HttpResponse<T>> supplier) {
        try {
            HttpResponse<T> response = supplier.get();
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Error: " + response.getStatus());
            }
            return response;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}

package seatsio.holdTokens;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class HoldTokens {

    private final String secretKey;
    private final String baseUrl;

    public HoldTokens(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public HoldToken create() {
        HttpResponse<String> response = stringResponse(Unirest.post(baseUrl + "/hold-tokens")
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken retrieve(String holdToken) {
        HttpResponse<String> response = stringResponse(Unirest.get(baseUrl + "/hold-tokens/{holdToken}")
                .routeParam("holdToken", holdToken)
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken expireInMinutes(String holdToken, int minutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", minutes).build();
        HttpResponse<String> response = stringResponse(Unirest.post(baseUrl + "/hold-tokens/{holdToken}")
                .basicAuth(secretKey, null)
                .routeParam("holdToken", holdToken)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

}

package seatsio.holdTokens;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.*;

public class HoldTokens {

    private final String secretKey;
    private final Long accountId;
    private final String baseUrl;

    public HoldTokens(String secretKey, Long accountId, String baseUrl) {
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.baseUrl = baseUrl;
    }

    public HoldToken create() {
        HttpResponse<String> response = stringResponse(post(baseUrl + "/hold-tokens", secretKey, accountId));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken create(int expiresInMinutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", expiresInMinutes).build();
        HttpResponse<String> response = stringResponse(post(baseUrl + "/hold-tokens", secretKey, accountId)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken retrieve(String holdToken) {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/hold-tokens/{holdToken}", secretKey, accountId)
                .routeParam("holdToken", holdToken));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken expireInMinutes(String holdToken, int minutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", minutes).build();
        HttpResponse<String> response = stringResponse(post(baseUrl + "/hold-tokens/{holdToken}", secretKey, accountId)
                .routeParam("holdToken", holdToken)
                .body(request.toString()));
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

}

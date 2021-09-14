package seatsio.holdTokens;

import com.google.gson.JsonObject;
import seatsio.util.UnirestWrapper;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.get;
import static seatsio.util.UnirestWrapper.post;

public class HoldTokens {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public HoldTokens(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public HoldToken create() {
        String response = unirest.stringResponse(post(baseUrl + "/hold-tokens"));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken create(int expiresInMinutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", expiresInMinutes).build();
        String response = unirest.stringResponse(post(baseUrl + "/hold-tokens")
                .body(request.toString()));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken retrieve(String holdToken) {
        String response = unirest.stringResponse(get(baseUrl + "/hold-tokens/{holdToken}")
                .routeParam("holdToken", holdToken));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken expireInMinutes(String holdToken, int minutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", minutes).build();
        String response = unirest.stringResponse(post(baseUrl + "/hold-tokens/{holdToken}")
                .routeParam("holdToken", holdToken)
                .body(request.toString()));
        return gson().fromJson(response, HoldToken.class);
    }

}

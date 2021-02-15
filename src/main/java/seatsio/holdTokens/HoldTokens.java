package seatsio.holdTokens;

import com.google.gson.JsonObject;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.*;

public class HoldTokens {

    private final String secretKey;
    private final String workspaceKey;
    private final String baseUrl;

    public HoldTokens(String secretKey, String workspaceKey, String baseUrl) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
    }

    public HoldToken create() {
        String response = stringResponse(post(baseUrl + "/hold-tokens", secretKey, workspaceKey));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken create(int expiresInMinutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", expiresInMinutes).build();
        String response = stringResponse(post(baseUrl + "/hold-tokens", secretKey, workspaceKey)
                .body(request.toString()));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken retrieve(String holdToken) {
        String response = stringResponse(get(baseUrl + "/hold-tokens/{holdToken}", secretKey, workspaceKey)
                .routeParam("holdToken", holdToken));
        return gson().fromJson(response, HoldToken.class);
    }

    public HoldToken expireInMinutes(String holdToken, int minutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", minutes).build();
        String response = stringResponse(post(baseUrl + "/hold-tokens/{holdToken}", secretKey, workspaceKey)
                .routeParam("holdToken", holdToken)
                .body(request.toString()));
        return gson().fromJson(response, HoldToken.class);
    }

}

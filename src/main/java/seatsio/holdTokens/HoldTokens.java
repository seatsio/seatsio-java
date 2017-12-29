package seatsio.holdTokens;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.lang.reflect.Type;
import java.time.Instant;

import static seatsio.UnirestUtil.unirest;
import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class HoldTokens {

    private final String secretKey;
    private final String baseUrl;

    public HoldTokens(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public HoldToken create() {
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/hold-tokens")
                .basicAuth(secretKey, null)
                .asString());
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken retrieve(String holdToken) {
        HttpResponse<String> response = unirest(() -> Unirest.get(baseUrl + "/hold-tokens/{holdToken}")
                .routeParam("holdToken", holdToken)
                .basicAuth(secretKey, null)
                .asString());
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    public HoldToken expireInMinutes(String holdToken, int minutes) {
        JsonObject request = aJsonObject().withProperty("expiresInMinutes", minutes).build();
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/hold-tokens/{holdToken}")
                .basicAuth(secretKey, null)
                .routeParam("holdToken", holdToken)
                .body(request.toString())
                .asString());
        return gson().fromJson(response.getBody(), HoldToken.class);
    }

    private static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, instantDeserializer())
                .create();
    }

    private static JsonDeserializer<Instant> instantDeserializer() {
        return (JsonElement json, Type typeOfT, JsonDeserializationContext ctx) -> Instant.parse(json.getAsJsonPrimitive().getAsString());
    }

}

package seatsio.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class SeatsioGson {

    public static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, instantDeserializer())
                .create();
    }

    private static JsonDeserializer<Instant> instantDeserializer() {
        return (JsonElement json, Type typeOfT, JsonDeserializationContext ctx) -> Instant.parse(json.getAsJsonPrimitive().getAsString());
    }
}

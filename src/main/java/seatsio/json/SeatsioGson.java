package seatsio.json;

import com.google.gson.*;
import seatsio.charts.CategoryKey;
import seatsio.events.Event;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;

public class SeatsioGson {

    public static Gson gson() {
        return gsonBuilder()
                .registerTypeAdapter(Event.class, new Event.EventJsonDeserializer())
                .create();
    }

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(CategoryKey.class, new CategoryKey.CategoryKeyJsonDeserializer())
                .registerTypeAdapter(CategoryKey.class, new CategoryKey.CategoryKeyJsonSerializer());
    }

    private static class InstantDeserializer implements JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Instant.parse(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {

        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }
}

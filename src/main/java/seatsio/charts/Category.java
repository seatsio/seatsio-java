package seatsio.charts;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import seatsio.util.ValueObject;

import java.lang.reflect.Type;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gsonBuilder;

public class Category extends ValueObject {

    private final CategoryKey key;
    private final String label;
    private final String color;
    private final Boolean accessible;

    public Category(CategoryKey key, String label, String color) {
        this(key, label, color, false);
    }

    public Category(String key, String label, String color, Boolean accessible) {
        this(CategoryKey.of(key), label, color, accessible);
    }

    public Category(Long key, String label, String color, Boolean accessible) {
        this(CategoryKey.of(key), label, color, accessible);
    }

    public Category(String key, String label, String color) {
        this(CategoryKey.of(key), label, color);
    }

    public Category(Long key, String label, String color) {
        this(CategoryKey.of(key), label, color);
    }

    public Category(CategoryKey key, String label, String color, Boolean accessible) {
        this.key = key;
        this.label = label;
        this.color = color;
        this.accessible = accessible;
    }

    public JsonObject toJson() {
        return aJsonObject()
                .withPropertyIfNotNull("key", this.key.toJson())
                .withPropertyIfNotNull("label", this.label)
                .withPropertyIfNotNull("color", this.color)
                .withPropertyIfNotNull("accessible", this.accessible)
                .build();
    }

    public static class CategoryJsonDeserializer implements JsonDeserializer<Category> {

        @Override
        public Category deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject category = jsonElement.getAsJsonObject();
            if (!category.has("accessible")) {
                category.addProperty("accessible", false);
            }
            return gsonBuilder().create().fromJson(category, Category.class);
        }
    }
}

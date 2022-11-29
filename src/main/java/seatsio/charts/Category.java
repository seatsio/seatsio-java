package seatsio.charts;

import com.google.gson.JsonObject;
import seatsio.util.ValueObject;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

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
}

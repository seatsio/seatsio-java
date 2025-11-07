package seatsio.charts;

import com.google.gson.JsonObject;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public record Category(CategoryKey key, String label, String color, Boolean accessible) {

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

    public JsonObject toJson() {
        return aJsonObject()
                .withPropertyIfNotNull("key", this.key.toJson())
                .withPropertyIfNotNull("label", this.label)
                .withPropertyIfNotNull("color", this.color)
                .withPropertyIfNotNull("accessible", this.accessible)
                .build();
    }
}

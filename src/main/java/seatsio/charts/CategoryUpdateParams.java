package seatsio.charts;

import com.google.gson.JsonObject;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class CategoryUpdateParams {

    private final String label;
    private final String color;
    private final Boolean accessible;

    public CategoryUpdateParams(String label,
                                String color,
                                Boolean accessible) {
        this.label = label;
        this.color = color;
        this.accessible = accessible;
    }

    public JsonObject toJson() {
        return aJsonObject()
                .withPropertyIfNotNull("label", label)
                .withPropertyIfNotNull("color", color)
                .withPropertyIfNotNull("accessible", accessible)
                .build();
    }
}

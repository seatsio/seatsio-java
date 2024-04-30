package seatsio.charts;

import com.google.gson.JsonObject;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class CategoryUpdateParams {

    private String label;
    private String color;
    private Boolean accessible;

    public CategoryUpdateParams() {
    }

    public CategoryUpdateParams(String label,
                                String color,
                                Boolean accessible) {
        this.label = label;
        this.color = color;
        this.accessible = accessible;
    }

    public CategoryUpdateParams withLabel(String label) {
        this.label = label;
        return this;
    }

    public CategoryUpdateParams withColor(String color) {
        this.color = color;
        return this;
    }

    public CategoryUpdateParams withAccessible(boolean accessible) {
        this.accessible = accessible;
        return this;
    }

    public JsonObject toJson() {
        return aJsonObject()
                .withPropertyIfNotNull("label", label)
                .withPropertyIfNotNull("color", color)
                .withPropertyIfNotNull("accessible", accessible)
                .build();
    }
}

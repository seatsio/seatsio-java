package seatsio.events;

import com.google.gson.JsonObject;

import java.util.Set;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public record Channel(String key, String name, String color, Integer index, Set<String> objects) {

    public JsonObject toJson() {
        return aJsonObject()
                .withProperty("key", key)
                .withProperty("name", name)
                .withProperty("color", color)
                .withPropertyIfNotNull("index", index)
                .withPropertyIfNotNull("objects", objects)
                .build();
    }
}

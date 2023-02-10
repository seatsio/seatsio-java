package seatsio.events;

import com.google.gson.JsonObject;

import java.util.Set;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class ChannelCreationParams {

    private final String key;
    private final String name;
    private final String color;
    private final int index;
    private final Set<String> objects;

    public ChannelCreationParams(String key, String name, String color, int index, Set<String> objects) {
        this.key = key;
        this.name = name;
        this.color = color;
        this.index = index;
        this.objects = objects;
    }

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

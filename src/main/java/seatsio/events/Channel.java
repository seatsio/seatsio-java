package seatsio.events;

import com.google.gson.JsonObject;
import seatsio.util.ValueObject;

import java.util.Set;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class Channel extends ValueObject {

    public String key;
    public final String name;
    public final String color;
    public final Integer index;
    public Set<String> objects;

    public Channel(String key, String name, String color, Integer index, Set<String> objects) {
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

package seatsio.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class JsonArrayBuilder {

    private Collection<JsonElement> items = new ArrayList<>();

    public static JsonArrayBuilder aJsonArray() {
        return new JsonArrayBuilder();
    }

    public JsonArrayBuilder withItems(JsonElement... items) {
        return withItems(Arrays.asList(items));
    }

    public JsonArrayBuilder withItems(String... items) {
        List<JsonElement> itemsAsJsonElement = stream(items)
                .map(JsonPrimitive::new)
                .collect(toList());
        return withItems(itemsAsJsonElement);
    }

    public JsonArrayBuilder withItems(Collection<JsonElement> items) {
        this.items = items;
        return this;
    }

    public JsonArray build() {
        JsonArray jsonArray = new JsonArray();
        for (JsonElement item : items) {
            jsonArray.add(item);
        }
        return jsonArray;
    }

    public String buildAsString() {
        return build().toString();
    }
}

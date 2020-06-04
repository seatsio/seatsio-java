package seatsio.json;

import com.google.gson.*;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Maps.transformValues;
import static java.util.stream.Collectors.toList;
import static seatsio.json.JsonArrayBuilder.aJsonArray;

public class JsonObjectBuilder {

    private Map<String, JsonElement> properties = new LinkedHashMap<>();

    public static JsonObjectBuilder aJsonObject() {
        return new JsonObjectBuilder();
    }

    public JsonObject build() {
        JsonObject o = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
            o.add(entry.getKey(), entry.getValue());
        }
        return o;
    }

    public String buildAsString() {
        return build().toString();
    }

    public JsonObjectBuilder withProperty(String propertyName, Boolean value) {
        return setValue(propertyName, value, JsonPrimitive::new);
    }

    public <E extends Enum> JsonObjectBuilder withProperty(String propertyName, E e) {
        return withProperty(propertyName, e.toString());
    }

    public JsonObjectBuilder withProperty(String propertyName, String value) {
        return setValue(propertyName, value, JsonPrimitive::new);
    }

    public JsonObjectBuilder withProperty(String propertyName, Number value) {
        return setValue(propertyName, value, JsonPrimitive::new);
    }

    public JsonObjectBuilder withProperty(String propertyName, Character value) {
        return setValue(propertyName, value, JsonPrimitive::new);
    }

    public JsonObjectBuilder withProperty(String propertyName, String[] values) {
        return withProperty(propertyName, Arrays.asList(values));
    }

    public JsonObjectBuilder withProperty(String propertyName, Collection<String> values) {
        return withProperty(propertyName, values, JsonPrimitive::new);
    }

    public <T extends JsonElement> JsonObjectBuilder withProperties(Map<String, T> properties) {
        properties.forEach(this::withProperty);
        return this;
    }

    public <T> JsonObjectBuilder withProperties(Map<String, T> properties, com.google.common.base.Function<T, JsonElement> f) {
        return this.withProperties(transformValues(properties, f));
    }


    public <T> JsonObjectBuilder withProperty(String propertyName, Collection<T> values, Function<T, JsonElement> f) {
        Collection<JsonElement> collect = values.stream().map(f).collect(toList());
        JsonArray array = aJsonArray().withItems(collect).build();
        return withProperty(propertyName, array);
    }

    public JsonObjectBuilder withPropertyIfNotNull(String propertyName, String value) {
        if (value == null) {
            return this;
        }
        return withProperty(propertyName, value);
    }

    public JsonObjectBuilder withPropertyIfNotNull(String propertyName, Boolean value) {
        if (value == null) {
            return this;
        }
        return withProperty(propertyName, value);
    }

    public <T> JsonObjectBuilder withPropertyIfNotNull(String propertyName, Collection<T> values, Function<T, JsonElement> f) {
        if (values == null) {
            return this;
        }
        return withProperty(propertyName, values, f);
    }

    public JsonObjectBuilder withPropertyIfNotNull(String propertyName, Collection<String> values) {
        if (values == null) {
            return this;
        }
        return withProperty(propertyName, values);
    }

    public JsonObjectBuilder withPropertyIfNotNull(String propertyName, Map<?, ?> value) {
        if (value == null) {
            return this;
        }
        return withProperty(propertyName, new Gson().toJsonTree(value));
    }

    private <T> JsonObjectBuilder setValue(String propertyName, T value, Function<T, JsonPrimitive> f) {
        if (value == null) {
            return withNullProperty(propertyName);
        }
        return withProperty(propertyName, f.apply(value));
    }

    private JsonObjectBuilder withNullProperty(String propertyName) {
        this.properties.put(propertyName, JsonNull.INSTANCE);
        return this;
    }

    public JsonObjectBuilder withProperty(String propertyName, JsonElement json) {
        this.properties.put(propertyName, json);
        return this;
    }

    public JsonObjectBuilder withoutProperty(String propertyName) {
        this.properties.remove(propertyName);
        return this;
    }

    public JsonObjectBuilder withProperty(String propertyName, Optional<String> optionalValue) {
        if (optionalValue.isPresent()) {
            return withProperty(propertyName, optionalValue.get());
        }
        return this;
    }
}

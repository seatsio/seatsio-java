package seatsio.charts;

import com.google.gson.*;
import seatsio.util.ValueObject;

import java.lang.reflect.Type;

abstract public class CategoryKey extends ValueObject {

    public static CategoryKey of(Long categoryKey) {
        if (categoryKey == null) {
            return null;
        }
        return new LongCategoryKey(categoryKey);
    }

    public static CategoryKey of(String categoryKey) {
        if (categoryKey == null) {
            return null;
        }
        return new StringCategoryKey(categoryKey);
    }

    public abstract boolean isLong();

    public abstract long toLong();

    public abstract String toString();

    public abstract JsonElement toJson();

    public static class CategoryKeyJsonSerializer implements JsonSerializer<CategoryKey> {

        @Override
        public JsonElement serialize(CategoryKey categoryKey, Type type, JsonSerializationContext jsonSerializationContext) {
            if (categoryKey == null) {
                return null;
            }
            return categoryKey.toJson();
        }
    }

    public static class CategoryKeyJsonDeserializer implements JsonDeserializer<CategoryKey> {

        @Override
        public CategoryKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return CategoryKey.of(primitive.getAsLong());
            } else {
                return CategoryKey.of(primitive.getAsString());
            }
        }
    }

}

package seatsio.charts;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringCategoryKey extends CategoryKey {

    private String key;

    public StringCategoryKey(String key) {
        this.key = key;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(key);
    }
}

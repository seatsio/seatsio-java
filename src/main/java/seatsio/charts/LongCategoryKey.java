package seatsio.charts;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class LongCategoryKey extends CategoryKey {

    private long key;

    public LongCategoryKey(long key) {
        this.key = key;
    }

    @Override
    public boolean isLong() {
        return true;
    }

    @Override
    public long toLong() {
        return key;
    }

    @Override
    public String toString() {
        return Long.toString(key);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(key);
    }
}

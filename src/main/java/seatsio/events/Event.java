package seatsio.events;

import com.google.gson.*;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.charts.StringCategoryKey;
import seatsio.seasons.Season;
import seatsio.util.ValueObject;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gsonBuilder;

public class Event extends ValueObject {

    public long id;
    public String key;
    public String chartKey;
    public String name;
    public LocalDate date;
    public ForSaleConfig forSaleConfig;
    public Boolean supportsBestAvailable;
    public Instant createdOn;
    public Instant updatedOn;
    public TableBookingConfig tableBookingConfig;
    public Channel[] channels;
    public String socialDistancingRulesetKey;
    public String topLevelSeasonKey;
    public boolean isTopLevelSeason;
    public boolean isPartialSeason;
    public boolean isEventInSeason;
    public Map<String, CategoryKey> objectCategories;
    public List<Category> categories;

    public boolean isSeason() {
        return false;
    }

    public static class EventJsonDeserializer implements JsonDeserializer<Event> {

        @Override
        public Event deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject event = jsonElement.getAsJsonObject();
            if (event.getAsJsonPrimitive("isSeason").getAsBoolean()) {
                return gsonBuilder().create().fromJson(event, Season.class);
            }
            return gsonBuilder().create().fromJson(event, Event.class);
        }
    }
}

package seatsio.events;

import com.google.gson.*;
import seatsio.seasons.Season;
import seatsio.util.ValueObject;

import java.lang.reflect.Type;
import java.time.Instant;

import static seatsio.json.SeatsioGson.gsonBuilder;

public class Event extends ValueObject {

    public long id;
    public String key;
    public String chartKey;
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

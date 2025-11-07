package seatsio.events;

import com.google.gson.*;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.seasons.Season;
import seatsio.util.ValueObject;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static seatsio.json.SeatsioGson.gsonBuilder;

public class Event extends ValueObject {

    private long id;
    private String key;
    private String chartKey;
    private String name;
    private LocalDate date;
    private ForSaleConfig forSaleConfig;
    private Boolean supportsBestAvailable;
    private Instant createdOn;
    private Instant updatedOn;
    private TableBookingConfig tableBookingConfig;
    private List<Channel> channels;
    private String topLevelSeasonKey;
    private boolean isTopLevelSeason;
    private boolean isPartialSeason;
    private boolean isEventInSeason;
    private Map<String, CategoryKey> objectCategories;
    private List<Category> categories;
    private boolean isInThePast;
    private List<String> partialSeasonKeysForEvent;
    private SeasonData season;

    public boolean isSeason() {
        return false;
    }

    public long id() {
        return id;
    }

    public String key() {
        return key;
    }

    public String chartKey() {
        return chartKey;
    }

    public String name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public ForSaleConfig forSaleConfig() {
        return forSaleConfig;
    }

    public Boolean supportsBestAvailable() {
        return supportsBestAvailable;
    }

    public Instant createdOn() {
        return createdOn;
    }

    public Instant updatedOn() {
        return updatedOn;
    }

    public TableBookingConfig tableBookingConfig() {
        return tableBookingConfig;
    }

    public List<Channel> channels() {
        return channels;
    }

    public String topLevelSeasonKey() {
        return topLevelSeasonKey;
    }

    public boolean isTopLevelSeason() {
        return isTopLevelSeason;
    }

    public boolean isPartialSeason() {
        return isPartialSeason;
    }

    public boolean isEventInSeason() {
        return isEventInSeason;
    }

    public Map<String, CategoryKey> objectCategories() {
        return objectCategories;
    }

    public List<Category> categories() {
        return categories;
    }

    public boolean isInThePast() {
        return isInThePast;
    }

    public List<String> partialSeasonKeysForEvent() {
        return partialSeasonKeysForEvent;
    }

    public SeasonData season() {
        return season;
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

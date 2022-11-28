package seatsio.events;

import seatsio.charts.Category;
import seatsio.charts.CategoryKey;

import java.util.List;
import java.util.Map;

public class EventCreationParamsBuilder {

    public String eventKey;
    public TableBookingConfig tableBookingConfig;
    public String socialDistancingRulesetKey;
    public Map<String, CategoryKey> objectCategories;
    public List<Category> categories;

    /**
     * Private. Use the static anEvent() initializer instead
     */
    private EventCreationParamsBuilder() {
    }

    public static EventCreationParamsBuilder anEvent() {
        return new EventCreationParamsBuilder();
    }

    public EventCreationParamsBuilder withKey(String eventKey) {
        this.eventKey = eventKey;
        return this;
    }

    public EventCreationParamsBuilder withTableBookingConfig(TableBookingConfig tableBookingConfig) {
        this.tableBookingConfig = tableBookingConfig;
        return this;
    }

    public EventCreationParamsBuilder withSocialDistancingRulesetKey(String socialDistancingRulesetKey) {
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
        return this;
    }

    public EventCreationParamsBuilder withObjectCategories(Map<String, CategoryKey> objectCategories) {
        this.objectCategories = objectCategories;
        return this;
    }

    public EventCreationParamsBuilder withCategories(List<Category> categories) {
        this.categories = categories;
        return this;
    }

    public EventCreationParams build() {
        return new EventCreationParams(eventKey, tableBookingConfig, socialDistancingRulesetKey, objectCategories, categories);
    }
}

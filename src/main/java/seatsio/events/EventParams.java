package seatsio.events;

import com.google.gson.JsonObject;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public abstract class EventParams<T extends EventParams> {

    public String eventKey;
    public String name;
    public LocalDate date;
    public TableBookingConfig tableBookingConfig;
    public String socialDistancingRulesetKey;
    public Map<String, CategoryKey> objectCategories;
    public List<Category> categories;

    public List<JsonObject> getCategoriesAsJson() {
        if (categories == null) {
            return null;
        }
        return categories.stream().map(Category::toJson).collect(toList());
    }

    public T withKey(String eventKey) {
        this.eventKey = eventKey;
        return (T) this;
    }

    public T withTableBookingConfig(TableBookingConfig tableBookingConfig) {
        this.tableBookingConfig = tableBookingConfig;
        return (T) this;
    }

    public T withSocialDistancingRulesetKey(String socialDistancingRulesetKey) {
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
        return (T) this;
    }

    public T withObjectCategories(Map<String, CategoryKey> objectCategories) {
        this.objectCategories = objectCategories;
        return (T) this;
    }

    public T withCategories(List<Category> categories) {
        this.categories = categories;
        return (T) this;
    }

    public T withName(String name) {
        this.name = name;
        return (T) this;
    }

    public T withDate(LocalDate date) {
        this.date = date;
        return (T) this;
    }
}

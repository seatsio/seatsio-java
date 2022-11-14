package seatsio.events;

import com.google.gson.JsonObject;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class EventCreationParams {

    public final String eventKey;
    public final TableBookingConfig tableBookingConfig;
    public final String socialDistancingRulesetKey;
    public final Map<String, CategoryKey> objectCategories;
    public final List<Category> categories;

    EventCreationParams(String eventKey, TableBookingConfig tableBookingConfig, String socialDistancingRulesetKey, Map<String, CategoryKey> objectCategories, List<Category> categories) {
        this.eventKey = eventKey;
        this.tableBookingConfig = tableBookingConfig;
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
        this.objectCategories = objectCategories;
        this.categories = categories;
    }

    public List<JsonObject> getCategoriesAsJson() {
        if (categories == null) {
            return null;
        }
        return categories.stream().map(Category::toJson).collect(toList());
    }
}

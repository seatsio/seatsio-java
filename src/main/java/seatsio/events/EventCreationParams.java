package seatsio.events;

import seatsio.charts.CategoryKey;

import java.util.Map;

public class EventCreationParams {

    public final String eventKey;
    public final TableBookingConfig tableBookingConfig;
    public final String socialDistancingRulesetKey;
    public final Map<String, CategoryKey> objectCategories;

    public EventCreationParams() {
        this.eventKey = null;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = null;
        this.objectCategories = null;
    }

    public EventCreationParams(String eventKey) {
        this.eventKey = eventKey;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = null;
        this.objectCategories = null;
    }

    public EventCreationParams(String eventKey, TableBookingConfig tableBookingConfig) {
        this.eventKey = eventKey;
        this.tableBookingConfig = tableBookingConfig;
        this.socialDistancingRulesetKey = null;
        this.objectCategories = null;
    }

    public EventCreationParams(String eventKey, String socialDistancingRulesetKey) {
        this.eventKey = eventKey;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
        this.objectCategories = null;
    }

    public EventCreationParams(String eventKey, Map<String, CategoryKey> objectCategories) {
        this.eventKey = eventKey;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = null;
        this.objectCategories = objectCategories;
    }
}

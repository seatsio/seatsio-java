package seatsio.events;

import java.util.Map;

public class EventCreationParams {

    public final String eventKey;
    public final Boolean bookWholeTables;
    public final Map<String, TableBookingMode> tableBookingModes;
    public final String socialDistancingRulesetKey;

    public EventCreationParams() {
        this.eventKey = null;
        this.bookWholeTables = null;
        this.tableBookingModes = null;
        this.socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey) {
        this.eventKey = eventKey;
        this.bookWholeTables = null;
        this.tableBookingModes = null;
        this.socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey, boolean bookWholeTables) {
        this.eventKey = eventKey;
        this.bookWholeTables = bookWholeTables;
        this.tableBookingModes = null;
        socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey, Map<String, TableBookingMode> tableBookingModes) {
        this.eventKey = eventKey;
        this.bookWholeTables = null;
        this.tableBookingModes = tableBookingModes;
        socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey, String socialDistancingRulesetKey) {
        this.eventKey = eventKey;
        this.bookWholeTables = null;
        this.tableBookingModes = null;
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
    }
}

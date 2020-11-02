package seatsio.events;

public class EventCreationParams {

    public final String eventKey;
    public final TableBookingConfig tableBookingConfig;
    public final String socialDistancingRulesetKey;

    public EventCreationParams() {
        this.eventKey = null;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey) {
        this.eventKey = eventKey;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey, TableBookingConfig tableBookingConfig) {
        this.eventKey = eventKey;
        this.tableBookingConfig = tableBookingConfig;
        socialDistancingRulesetKey = null;
    }

    public EventCreationParams(String eventKey, String socialDistancingRulesetKey) {
        this.eventKey = eventKey;
        this.tableBookingConfig = null;
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
    }
}

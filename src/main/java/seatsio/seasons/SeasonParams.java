package seatsio.seasons;

import seatsio.events.TableBookingConfig;

import java.util.List;

public class SeasonParams {

    private String key;
    private List<String> eventKeys;
    private Integer numberOfEvents;
    private TableBookingConfig tableBookingConfig;
    private String socialDistancingRulesetKey;

    public SeasonParams() {
    }

    public String key() {
        return key;
    }

    public SeasonParams key(String key) {
        this.key = key;
        return this;
    }

    public SeasonParams eventKeys(List<String> eventKeys) {
        this.eventKeys = eventKeys;
        return this;
    }

    public List<String> eventKeys() {
        return eventKeys;
    }

    public SeasonParams numberOfEvents(Integer numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
        return this;
    }

    public Integer numberOfEvents() {
        return numberOfEvents;
    }

    public SeasonParams tableBookingConfig(TableBookingConfig tableBookingConfig) {
        this.tableBookingConfig = tableBookingConfig;
        return this;
    }

    public TableBookingConfig tableBookingConfig() {
        return tableBookingConfig;
    }

    public String socialDistancingRulesetKey() {
        return socialDistancingRulesetKey;
    }

    public SeasonParams socialDistancingRulesetKey(String socialDistancingRulesetKey) {
        this.socialDistancingRulesetKey = socialDistancingRulesetKey;
        return this;
    }
}

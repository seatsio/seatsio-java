package seatsio.seasons;

import com.google.gson.JsonObject;
import seatsio.events.Channel;
import seatsio.events.ForSaleConfig;
import seatsio.events.TableBookingConfig;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SeasonParams {

    private String key;
    private List<String> eventKeys;
    private Integer numberOfEvents;
    private TableBookingConfig tableBookingConfig;
    private List<Channel> channels;
    private ForSaleConfig forSaleConfig;

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

    public SeasonParams channels(List<Channel> channels) {
        this.channels = channels;
        return this;
    }

    public SeasonParams forSaleConfig(ForSaleConfig forSaleConfig) {
        this.forSaleConfig = forSaleConfig;
        return this;
    }

    public List<JsonObject> getChannelsAsJson() {
        if (channels == null) {
            return null;
        }
        return channels.stream().map(Channel::toJson).collect(toList());
    }

    public JsonObject getForSaleConfigAsJson() {
        if (forSaleConfig == null) {
            return null;
        }
        return forSaleConfig.toJson();
    }
}

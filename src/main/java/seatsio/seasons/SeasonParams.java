package seatsio.seasons;

import com.google.gson.JsonObject;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.events.Channel;
import seatsio.events.ForSaleConfigParams;
import seatsio.events.TableBookingConfig;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unchecked")
public abstract class SeasonParams<T extends  SeasonParams<?>> {

    private String key;
    private String name;
    private List<String> eventKeys;
    private Integer numberOfEvents;
    private TableBookingConfig tableBookingConfig;
    private List<Channel> channels;
    private Map<String, CategoryKey> objectCategories;
    private List<Category> categories;
    private ForSaleConfigParams forSaleConfigParams;
    private Boolean forSalePropagated;

    public String key() {
        return key;
    }

    public T key(String key) {
        this.key = key;
        return (T) this;
    }

    public String name() {
        return name;
    }

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public T eventKeys(List<String> eventKeys) {
        this.eventKeys = eventKeys;
        return (T) this;
    }

    public List<String> eventKeys() {
        return eventKeys;
    }

    public T numberOfEvents(Integer numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
        return (T) this;
    }

    public Integer numberOfEvents() {
        return numberOfEvents;
    }

    public T tableBookingConfig(TableBookingConfig tableBookingConfig) {
        this.tableBookingConfig = tableBookingConfig;
        return (T) this;
    }

    public TableBookingConfig tableBookingConfig() {
        return tableBookingConfig;
    }

    public T channels(List<Channel> channels) {
        this.channels = channels;
        return (T) this;
    }

    public Map<String, CategoryKey> objectCategories() {
        return objectCategories;
    }

    public T withObjectCategories(Map<String, CategoryKey> objectCategories) {
        this.objectCategories = objectCategories;
        return (T) this;
    }

    public List<JsonObject> categoriesAsJson() {
        if (categories == null) {
            return null;
        }
        return categories.stream().map(Category::toJson).collect(toList());
    }

    public List<Category> categories() {
        return categories;
    }

    public T withCategories(List<Category> categories) {
        this.categories = categories;
        return (T) this;
    }

    public T forSaleConfigParams(ForSaleConfigParams forSaleConfigParams) {
        this.forSaleConfigParams = forSaleConfigParams;
        return (T) this;
    }

    public Boolean forSalePropagated() {
        return forSalePropagated;
    }

    public T forSalePropagated(Boolean forSalePropagated) {
        this.forSalePropagated = forSalePropagated;
        return (T) this;
    }

    public List<JsonObject> getChannelsAsJson() {
        if (channels == null) {
            return null;
        }
        return channels.stream().map(Channel::toJson).collect(toList());
    }

    public JsonObject getForSaleConfigAsJson() {
        if (forSaleConfigParams == null) {
            return null;
        }
        return forSaleConfigParams.toJson();
    }
}

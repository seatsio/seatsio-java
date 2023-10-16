package seatsio.events;

import com.google.gson.JsonObject;
import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class ForSaleConfig extends ValueObject {

    public boolean forSale;
    public List<String> objects;
    public Map<String, Integer> areaPlaces;
    public List<String> categories;

    public ForSaleConfig() {
    }

    public ForSaleConfig(boolean forSale, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        this.forSale = forSale;
        this.objects = objects;
        this.areaPlaces = areaPlaces;
        this.categories = categories;
    }

    public ForSaleConfig forSale(boolean forSale) {
        this.forSale = forSale;
        return this;
    }

    public ForSaleConfig objects(List<String> objects) {
        this.objects = objects;
        return this;
    }

    public ForSaleConfig areaPlaces(Map<String, Integer> areaPlaces) {
        this.areaPlaces = areaPlaces;
        return this;
    }

    public ForSaleConfig categories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public JsonObject toJson() {
        return aJsonObject()
                .withProperty("forSale", forSale)
                .withPropertyIfNotNull("objects", objects)
                .withPropertyIfNotNull("areaPlaces", areaPlaces)
                .withPropertyIfNotNull("categories", categories)
                .build();
    }
}

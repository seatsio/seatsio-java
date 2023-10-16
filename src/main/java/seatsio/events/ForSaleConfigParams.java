package seatsio.events;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class ForSaleConfigParams {

    public boolean forSale;
    public List<String> objects;
    public Map<String, Integer> areaPlaces;
    public List<String> categories;

    public ForSaleConfigParams(boolean forSale) {
        this.forSale = forSale;
    }

    public ForSaleConfigParams(boolean forSale, List<String> objects, Map<String, Integer> areaPlaces, List<String> categories) {
        this.forSale = forSale;
        this.objects = objects;
        this.areaPlaces = areaPlaces;
        this.categories = categories;
    }

    public ForSaleConfigParams forSale(boolean forSale) {
        this.forSale = forSale;
        return this;
    }

    public ForSaleConfigParams objects(List<String> objects) {
        this.objects = objects;
        return this;
    }

    public ForSaleConfigParams areaPlaces(Map<String, Integer> areaPlaces) {
        this.areaPlaces = areaPlaces;
        return this;
    }

    public ForSaleConfigParams categories(List<String> categories) {
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

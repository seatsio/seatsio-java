package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class ForSaleConfig extends ValueObject {

    public boolean forSale;
    public List<String> objects;
    public Map<String, Integer> areaPlaces;
    public List<String> categories;

}

package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;

public class ForSaleConfig extends ValueObject {

    public boolean forSale;
    public List<String> objects;
    public List<String> categories;

}

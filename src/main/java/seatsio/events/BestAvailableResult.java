package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;

public class BestAvailableResult extends ValueObject {

    public boolean nextToEachOther;
    public List<String> objects;
}

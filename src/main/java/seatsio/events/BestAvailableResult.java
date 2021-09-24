package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailableResult extends ValueObject {

    public List<String> objects;
    public Map<String, EventObjectInfo> objectDetails;
    public boolean nextToEachOther;

}

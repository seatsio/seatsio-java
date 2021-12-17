package seatsio.seasons;

import seatsio.events.Event;
import seatsio.util.ValueObject;

import java.util.List;

public class Season extends ValueObject {

    public long id;
    public String key;
    public Event seasonEvent;
    public List<Event> events;
}

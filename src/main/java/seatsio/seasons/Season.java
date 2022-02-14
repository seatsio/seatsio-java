package seatsio.seasons;

import seatsio.events.Event;

import java.util.List;

public class Season extends Event {

    public List<Event> events;
    public List<String> partialSeasonKeys;

    public boolean isSeason() {
        return true;
    }
}

package seatsio.charts;

import seatsio.events.Event;
import seatsio.util.ValueObject;

import java.util.List;
import java.util.Set;

public class Chart extends ValueObject {

    public long id;
    public String key;
    public String name;
    public String status;
    public Set<String> tags;
    public String publishedVersionThumbnailUrl;
    public String draftVersionThumbnailUrl;
    public List<Event> events;
    public boolean archived;
    public ChartValidationResult validation;
    public String venueType;
    public List<Zone> zones;
}

package seatsio.charts;

import seatsio.events.Event;

import java.util.List;
import java.util.Set;

public record Chart(long id, String key, String name, String status, Set<String> tags,
                    String publishedVersionThumbnailUrl, String draftVersionThumbnailUrl, List<Event> events,
                    boolean archived, ChartValidationResult validation, String venueType, List<Zone> zones) {

}

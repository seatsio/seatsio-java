package seatsio.events;

import java.util.Map;
import java.util.Set;

public record Channel(String key, String id, String name, String color, Integer index, Set<String> objects, Map<String, Integer> areaPlaces) {

    public String areaPartitionLabel(String areaLabel) {
        return areaLabel + "##" + id;
    }
}

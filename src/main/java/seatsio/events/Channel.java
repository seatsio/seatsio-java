package seatsio.events;

import java.util.Map;
import java.util.Set;

public record Channel(String key, String name, String color, Integer index, Set<String> objects, Map<String, Integer> areaPlaces, String publicKey) {
}

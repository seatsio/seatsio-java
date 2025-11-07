package seatsio.events;

import java.util.Map;

public record ChangeObjectStatusResult(Map<String, EventObjectInfo> objects) {

}

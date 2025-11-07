package seatsio.events;

import java.util.List;
import java.util.Map;

public record BestAvailableResult(List<String> objects, Map<String, EventObjectInfo> objectDetails,
                                  boolean nextToEachOther) {

}

package seatsio.eventLog;

import java.time.Instant;
import java.util.Map;

public record EventLogItem(long id, String type, Instant timestamp, Map<?, ?> data) {

}

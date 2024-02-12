package seatsio.eventLog;

import java.time.Instant;
import java.util.Map;

public class EventLogItem {

    public long id;
    public String type;
    public Instant timestamp;
    public Map<?, ?> data;
}

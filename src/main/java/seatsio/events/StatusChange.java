package seatsio.events;

import java.time.Instant;
import java.util.Map;

public class StatusChange {

    public long id;
    public long eventId;
    public String status;
    public int quantity;
    public String objectLabelOrUuid;
    public Instant date;
    public String orderId;
    public Map<?, ?> extraData;
}

package seatsio.events;

import seatsio.util.ValueObject;

import java.time.Instant;
import java.util.Map;

public class ObjectStatus extends ValueObject {

    public static final String FREE = "free";
    public static final String BOOKED = "booked";
    public static final String HELD = "reservedByToken";

    public String status;
    public String holdToken;
    public String orderId;
    public String ticketType;
    public int quantity;
    public Map<?, ?> extraData;
}

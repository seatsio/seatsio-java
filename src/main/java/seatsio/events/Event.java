package seatsio.events;

import seatsio.util.ValueObject;

import java.time.Instant;

public class Event extends ValueObject {

    public long id;
    public String key;
    public String chartKey;
    public ForSaleConfig forSaleConfig;
    public Boolean supportsBestAvailable;
    public Instant createdOn;
    public Instant updatedOn;
    public TableBookingConfig tableBookingConfig;
    public Channel[] channels;
    public String socialDistancingRulesetKey;
}

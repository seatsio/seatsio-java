package seatsio.events;

import seatsio.util.ValueObject;

import java.time.Instant;
import java.util.Map;

public class Event extends ValueObject {

    public long id;
    public String key;
    public String chartKey;
    public ForSaleConfig forSaleConfig;
    public boolean bookWholeTables;
    public boolean supportsBestAvailable;
    public Instant createdOn;
    public Instant updatedOn;
    public Map<String, TableBookingMode> tableBookingModes;
}

package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailable extends ValueObject {

    private final int number;
    private final List<String> categories;
    private final String zone;
    private final List<Map<String, Object>> extraData;
    private final List<String> ticketTypes;
    private final Boolean tryToPreventOrphanSeats;

    public BestAvailable(int number) {
        this(number, null, null, null, null, null);
    }

    public BestAvailable(int number, List<String> categories) {
        this(number, categories, null, null, null, null);
    }

    BestAvailable(int number, List<String> categories, String zone, List<Map<String, Object>> extraData, List<String> ticketTypes, Boolean tryToPreventOrphanSeats) {
        this.number = number;
        this.categories = categories;
        this.zone = zone;
        this.extraData = extraData;
        this.ticketTypes = ticketTypes;
        this.tryToPreventOrphanSeats = tryToPreventOrphanSeats;
    }
}

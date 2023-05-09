package seatsio.events;

import java.util.List;
import java.util.Map;

public class BestAvailableBuilder {

    private int number;
    private List<String> categories;
    private List<Map<String, Object>> extraData;
    private List<String> ticketTypes;
    private Boolean tryToPreventOrphanSeats;

    /**
     * Private. Use the static someBestAvailable() initializer instead
     */
    private BestAvailableBuilder() {
    }

    public static BestAvailableBuilder someBestAvailable() {
        return new BestAvailableBuilder();
    }

    public BestAvailableBuilder withNumber(int number) {
        this.number = number;
        return this;
    }

    public BestAvailableBuilder withCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public BestAvailableBuilder withExtraData(List<Map<String, Object>> extraData) {
        this.extraData = extraData;
        return this;
    }

    public BestAvailableBuilder withTicketTypes(List<String> ticketTypes) {
        this.ticketTypes = ticketTypes;
        return this;
    }

    public BestAvailableBuilder withTryToPreventOrphanSeats(Boolean tryToPreventOrphanSeats) {
        this.tryToPreventOrphanSeats = tryToPreventOrphanSeats;
        return this;
    }

    public BestAvailable build() {
        return new BestAvailable(
                this.number,
                this.categories,
                this.extraData,
                this.ticketTypes,
                this.tryToPreventOrphanSeats
        );
    }
}

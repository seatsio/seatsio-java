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

    public static class Builder {

        private int number;
        private List<String> categories;
        private String zone;
        private List<Map<String, Object>> extraData;
        private List<String> ticketTypes;
        private Boolean tryToPreventOrphanSeats;

        public Builder() {
        }

        public Builder withNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder withCategories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder withZone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder withExtraData(List<Map<String, Object>> extraData) {
            this.extraData = extraData;
            return this;
        }

        public Builder withTicketTypes(List<String> ticketTypes) {
            this.ticketTypes = ticketTypes;
            return this;
        }

        public Builder withTryToPreventOrphanSeats(Boolean tryToPreventOrphanSeats) {
            this.tryToPreventOrphanSeats = tryToPreventOrphanSeats;
            return this;
        }

        public BestAvailable build() {
            return new BestAvailable(
                    this.number,
                    this.categories,
                    this.zone,
                    this.extraData,
                    this.ticketTypes,
                    this.tryToPreventOrphanSeats
            );
        }
    }
}

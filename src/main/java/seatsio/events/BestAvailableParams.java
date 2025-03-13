package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailableParams extends ValueObject {

    private final int number;
    private final List<String> categories;
    private final String zone;
    private final List<String> sections;
    private final List<Map<String, Object>> extraData;
    private final List<String> ticketTypes;
    private final Boolean tryToPreventOrphanSeats;
    private final Integer accessibleSeats;

    public BestAvailableParams(int number) {
        this(number, null, null, null, null, null, null, null);
    }

    public BestAvailableParams(int number, List<String> categories) {
        this(number, categories, null, null, null, null, null, null);
    }

    BestAvailableParams(int number, List<String> categories, String zone, List<String> sections, List<Map<String, Object>> extraData, List<String> ticketTypes, Boolean tryToPreventOrphanSeats, Integer accessibleSeats) {
        this.number = number;
        this.categories = categories;
        this.zone = zone;
        this.sections = sections;
        this.extraData = extraData;
        this.ticketTypes = ticketTypes;
        this.tryToPreventOrphanSeats = tryToPreventOrphanSeats;
        this.accessibleSeats = accessibleSeats;
    }

    public static class Builder {

        private int number;
        private List<String> categories;
        private String zone;
        private List<String> sections;
        private List<Map<String, Object>> extraData;
        private List<String> ticketTypes;
        private Boolean tryToPreventOrphanSeats;
        private Integer accessibleSeats;

        public Builder withNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder withAccessibleSeats(Integer accessibleSeats) {
            this.accessibleSeats = accessibleSeats;
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

        public Builder withSections(List<String> sections) {
            this.sections = sections;
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

        public BestAvailableParams build() {
            return new BestAvailableParams(
                    this.number,
                    this.categories,
                    this.zone,
                    this.sections,
                    this.extraData,
                    this.ticketTypes,
                    this.tryToPreventOrphanSeats,
                    this.accessibleSeats
            );
        }
    }
}

package seatsio.events;

import java.util.Map;

public record TableBookingConfig(String mode, Map<String, TableBookingMode> tables) {

    public static TableBookingConfig inherit() {
        return new TableBookingConfig("INHERIT", null);
    }

    public static TableBookingConfig allBySeat() {
        return new TableBookingConfig("ALL_BY_SEAT", null);
    }

    public static TableBookingConfig allByTable() {
        return new TableBookingConfig("ALL_BY_TABLE", null);
    }

    public static TableBookingConfig custom(Map<String, TableBookingMode> tables) {
        return new TableBookingConfig("CUSTOM", tables);
    }
}

package seatsio.events;

import seatsio.util.ValueObject;

import java.util.Map;

public class TableBookingConfig extends ValueObject {

    public final Map<String, TableBookingMode> tables;
    public final String mode;

    private TableBookingConfig(String mode, Map<String, TableBookingMode> tables) {
        this.mode = mode;
        this.tables = tables;
    }

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

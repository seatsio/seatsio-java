package seatsio.events;

import java.util.Map;

public class EventCreationParams {

    public final String eventKey;
    public final Boolean bookWholeTables;
    public final Map<String, TableBookingMode> tableBookingModes;

    public EventCreationParams() {
        eventKey = null;
        bookWholeTables = null;
        tableBookingModes = null;
    }

    public EventCreationParams(String eventKey) {
        this.eventKey = eventKey;
        bookWholeTables = null;
        tableBookingModes = null;
    }

    public EventCreationParams(String eventKey, boolean bookWholeTables) {
        this.eventKey = eventKey;
        this.bookWholeTables = bookWholeTables;
        tableBookingModes = null;
    }

    public EventCreationParams(String eventKey, Map<String, TableBookingMode> tableBookingModes) {
        this.eventKey = eventKey;
        this.tableBookingModes = tableBookingModes;
        this.bookWholeTables = null;
    }
}

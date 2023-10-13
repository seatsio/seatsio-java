package seatsio.reports.usage.detailsForEventInMonth;

import seatsio.util.ValueObject;

import java.time.Instant;

public class UsageForObjectV1 extends ValueObject {

    public final String object;
    public final int numFirstBookings;
    public final Instant firstBookingDate;
    public final int numFirstSelections;
    public final int numFirstBookingsOrSelections;

    public UsageForObjectV1(String object, int numFirstBookings, Instant firstBookingDate, int numFirstSelections, int numFirstBookingsOrSelections) {
        this.object = object;
        this.numFirstBookings = numFirstBookings;
        this.firstBookingDate = firstBookingDate;
        this.numFirstSelections = numFirstSelections;
        this.numFirstBookingsOrSelections = numFirstBookingsOrSelections;
    }
}

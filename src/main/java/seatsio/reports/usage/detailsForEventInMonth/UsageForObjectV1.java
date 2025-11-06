package seatsio.reports.usage.detailsForEventInMonth;

import java.time.Instant;

public record UsageForObjectV1(String object, int numFirstBookings, Instant firstBookingDate, int numFirstSelections,
                               int numFirstBookingsOrSelections) {

}

package seatsio.reports.usage.summaryForMonths;

import seatsio.reports.usage.Month;

import java.util.Map;

public class UsageSummaryForMonth {

    public Month month;
    public int numUsedObjects;
    public int numFirstBookings;
    public Map<String, Integer> numFirstBookingsByStatus;
    public int numFirstBookingsOrSelections;
}

package seatsio.reports.usage.summaryForMonths;

import seatsio.reports.usage.Month;

public record UsageSummaryForMonth(Month month, int numUsedObjects) {

}

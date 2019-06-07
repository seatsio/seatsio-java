package seatsio.reports.usage;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Event;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObject;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForMonth;

import java.util.List;

public class UsageReportsTest extends SeatsioClientTest {

    @Test
    public void summaryForAllMonths() {
        List<UsageSummaryForMonth> report = client.usageReports.summaryForAllMonths();
    }

    @Test
    public void detailsForMonth() {
        List<UsageDetails> report = client.usageReports.detailsForMonth(new Month(2019, 5));
    }

    @Test
    public void detailsForEventInMonth() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        List<UsageForObject> report = client.usageReports.detailsForEventInMonth(event.id, new Month(2019, 5));
    }
}

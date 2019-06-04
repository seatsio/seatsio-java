package seatsio.reports.usage;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Event;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObject;
import seatsio.reports.usage.summaryForMonths.Month;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForMonth;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class usageReportsTest extends SeatsioClientTest {

    @Test
    public void allMonths() {
        List<UsageSummaryForMonth> report = client.usageReports.summaryForAllMonths();

        assertThat(report).isNotEmpty();
    }

    @Test
    public void month() {
        List<UsageDetails> report = client.usageReports.detailsForMonth(new Month(2019, 5));

        assertThat(report).isEmpty();
    }

    @Test
    public void eventInMonth() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        List<UsageForObject> report = client.usageReports.detailsForEventInMonth(event.id, new Month(2019, 5));

        assertThat(report).isEmpty();
    }
}

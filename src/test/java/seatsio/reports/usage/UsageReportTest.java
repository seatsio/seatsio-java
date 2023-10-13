package seatsio.reports.usage;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObjectV1;
import seatsio.reports.usage.detailsForMonth.UsageByEvent;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.detailsForMonth.UsageEvent;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForAllMonths;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class UsageReportTest extends SeatsioClientTest {

    @Test
    public void usageReportForAllMonths() {
        assumeTrue(isDemoCompanySecretKeySet());

        SeatsioClient client = seatsioClient(demoCompanySecretKey());

        UsageSummaryForAllMonths report = client.usageReports.summaryForAllMonths();

        assertThat(report.usageCutoffDate).isNotNull();
        assertThat(report.usage.size()).isGreaterThan(0);
        assertThat(report.usage.get(0).month).isEqualTo(new Month(2014, 2));
    }

    @Test
    public void usageReportForMonth() {
        assumeTrue(isDemoCompanySecretKeySet());

        SeatsioClient client = seatsioClient(demoCompanySecretKey());

        List<UsageDetails> report = client.usageReports.detailsForMonth(new Month(2021, 11));

        assertThat(report.size()).isGreaterThan(0);
        assertThat(report.get(0).usageByChart.size()).isGreaterThan(0);
        assertThat(report.get(0).usageByChart.get(0).usageByEvent).containsExactly(new UsageByEvent(
                new UsageEvent(580293, "largeStadiumEvent", false), 143
        ));
    }

    @Test
    public void usageReportForEventInMonth() {
        assumeTrue(isDemoCompanySecretKeySet());

        SeatsioClient client = seatsioClient(demoCompanySecretKey());

        List<?> report = client.usageReports.detailsForEventInMonth(580293, new Month(2021, 11));

        assertThat(report.size()).isGreaterThan(0);
        assertThat((UsageForObjectV1) report.get(0)).isEqualTo(new UsageForObjectV1("102-9-14", 0, null, 1, 1));
    }
}

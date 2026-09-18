package seatsio.reports.usage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import seatsio.SeatsioClient;
import seatsio.reports.usage.detailsForEventInMonth.UsageForObjectV1;
import seatsio.reports.usage.detailsForMonth.UsageByEvent;
import seatsio.reports.usage.detailsForMonth.UsageDetails;
import seatsio.reports.usage.detailsForMonth.UsageEvent;
import seatsio.reports.usage.summaryForMonths.UsageSummaryForAllMonths;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Execution(ExecutionMode.CONCURRENT)
public class UsageReportTest {

    @Test
    public void usageReportForAllMonths() {
        assumeTrue(isConfigured());

        SeatsioClient client = usageReportingClient();

        UsageSummaryForAllMonths report = client.usageReports.summaryForAllMonths();

        assertThat(report.usageCutoffDate()).isNotNull();
        assertThat(report.usage().size()).isGreaterThan(0);
        assertThat(report.usage().get(0).month()).isEqualTo(new Month(2014, 2));
    }

    @Test
    public void usageReportForMonth() {
        assumeTrue(isConfigured());

        SeatsioClient client = usageReportingClient();

        List<UsageDetails> report = client.usageReports.detailsForMonth(new Month(2021, 11));

        assertThat(report.size()).isGreaterThan(0);
        assertThat(report.get(0).usageByChart().size()).isGreaterThan(0);
        assertThat(report.get(0).usageByChart().get(0).usageByEvent()).containsExactly(new UsageByEvent(
                new UsageEvent(580293, "largeStadiumEvent", false), 143
        ));
    }

    @Test
    public void usageReportForEventInMonth() {
        assumeTrue(isConfigured());

        SeatsioClient client = usageReportingClient();

        List<?> report = client.usageReports.detailsForEventInMonth(580293, new Month(2021, 11));

        assertThat(report.size()).isGreaterThan(0);
        assertThat((UsageForObjectV1) report.get(0)).isEqualTo(new UsageForObjectV1("102-9-14", 0, null, 1, 1));
    }

    private static SeatsioClient usageReportingClient() {
        return new SeatsioClient(secretKey(), null, apiUrl());
    }

    private static String apiUrl() {
        return System.getenv("USAGE_REPORTING_TESTS_API_URL");
    }

    private static String secretKey() {
        return System.getenv("USAGE_REPORTING_TESTS_SECRET_KEY");
    }

    private static boolean isConfigured() {
        return isSet(secretKey()) && isSet(apiUrl());
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }
}

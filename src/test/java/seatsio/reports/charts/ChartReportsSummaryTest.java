package seatsio.reports.charts;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.NO_SECTION;
import static seatsio.reports.charts.ChartReportSummaryItemBuilder.aChartReportSummaryItem;

public class ChartReportsSummaryTest extends SeatsioClientTest {

    @Test
    public void summaryByObjectType() {
        String chartKey = createTestChart();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByObjectType(chartKey);

        ChartReportSummaryItem seatReport = aChartReportSummaryItem()
                .withCount(32)
                .withBySection(ImmutableMap.of(NO_SECTION, 32))
                .withByCategoryKey(ImmutableMap.of("9", 16, "10", 16))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 16, "Cat2", 16))
                .build();
        ChartReportSummaryItem generalAdmissionReport = aChartReportSummaryItem()
                .withCount(200)
                .withBySection(ImmutableMap.of(NO_SECTION, 200))
                .withByCategoryKey(ImmutableMap.of("9", 100, "10", 100))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 100, "Cat2", 100))
                .build();
        ChartReportSummaryItem emptyReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(new HashMap<>())
                .withByCategoryKey(new HashMap<>())
                .withByCategoryLabel(new HashMap<>())
                .build();

        assertThat(report).isEqualTo(ImmutableMap.of(
                "seat", seatReport,
                "generalAdmission", generalAdmissionReport,
                "booth", emptyReport,
                "table", emptyReport
        ));
    }

    @Test
    public void summaryByCategoryKey() {
        String chartKey = createTestChart();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByCategoryKey(chartKey);

        ChartReportSummaryItem cat9Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of(NO_SECTION, 116))
                .withByObjectType(ImmutableMap.of("seat", 16, "generalAdmission", 100))
                .build();
        ChartReportSummaryItem cat10Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of(NO_SECTION, 116))
                .withByObjectType(ImmutableMap.of("seat", 16, "generalAdmission", 100))
                .build();
        ChartReportSummaryItem noCategoryReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(new HashMap<>())
                .withByObjectType(new HashMap<>())
                .build();

        assertThat(report).isEqualTo(ImmutableMap.of("9", cat9Report, "10", cat10Report, "NO_CATEGORY", noCategoryReport));
    }

    @Test
    public void summaryByCategoryLabel() {
        String chartKey = createTestChart();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByCategoryLabel(chartKey);

        ChartReportSummaryItem cat1Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of(NO_SECTION, 116))
                .withByObjectType(ImmutableMap.of("seat", 16, "generalAdmission", 100))
                .build();
        ChartReportSummaryItem cat2Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of(NO_SECTION, 116))
                .withByObjectType(ImmutableMap.of("seat", 16, "generalAdmission", 100))
                .build();
        ChartReportSummaryItem noCategoryReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(new HashMap<>())
                .withByObjectType(new HashMap<>())
                .build();

        assertThat(report).isEqualTo(ImmutableMap.of("Cat1", cat1Report, "Cat2", cat2Report, "NO_CATEGORY", noCategoryReport));
    }

    @Test
    public void summaryBySection() {
        String chartKey = createTestChart();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryBySection(chartKey);

        ChartReportSummaryItem noSectionReport = aChartReportSummaryItem()
                .withCount(232)
                .withByCategoryKey(ImmutableMap.of("9", 116, "10", 116))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 116, "Cat2", 116))
                .withByObjectType(ImmutableMap.of("seat", 32, "generalAdmission", 200))
                .build();

        assertThat(report).isEqualTo(ImmutableMap.of(NO_SECTION, noSectionReport));
    }
}

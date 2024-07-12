package seatsio.reports.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.NO_SECTION;
import static seatsio.events.EventObjectInfo.NO_ZONE;
import static seatsio.reports.charts.ChartReportBookWholeTablesMode.TRUE;
import static seatsio.reports.charts.ChartReportOptions.options;
import static seatsio.reports.charts.ChartReportSummaryItemBuilder.aChartReportSummaryItem;

public abstract class AbstractChartReportsSummaryTest extends SeatsioClientTest {

    @Test
    public void summaryByObjectType() {
        String chartKey = createTestChartForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByObjectType(chartKey, updateOptions());

        ChartReportSummaryItem seatReport = aChartReportSummaryItem()
                .withCount(32)
                .withBySection(Map.of(NO_SECTION, 32))
                .withByCategoryKey(Map.of("9", 16, "10", 16))
                .withByCategoryLabel(Map.of("Cat1", 16, "Cat2", 16))
                .withByZone(Map.of(NO_ZONE, 32))
                .build();
        ChartReportSummaryItem generalAdmissionReport = aChartReportSummaryItem()
                .withCount(200)
                .withBySection(Map.of(NO_SECTION, 200))
                .withByCategoryKey(Map.of("9", 100, "10", 100))
                .withByCategoryLabel(Map.of("Cat1", 100, "Cat2", 100))
                .withByZone(Map.of(NO_ZONE, 200))
                .build();
        ChartReportSummaryItem emptyReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByZone(Map.of())
                .build();

        assertThat(report).isEqualTo(Map.of(
                "seat", seatReport,
                "generalAdmission", generalAdmissionReport,
                "booth", emptyReport,
                "table", emptyReport
        ));
    }

    @Test
    public void summaryByObjectType_bookWholeTablesTrue() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByObjectType(chartKey, updateOptions(options().bookWholeTablesMode(TRUE)));

        ChartReportSummaryItem tablesReport = aChartReportSummaryItem()
                .withCount(2)
                .withBySection(Map.of(NO_SECTION, 2))
                .withByCategoryKey(Map.of("9", 2))
                .withByCategoryLabel(Map.of("Cat1", 2))
                .withByZone(Map.of(NO_ZONE, 2))
                .build();
        ChartReportSummaryItem emptyReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByCategoryLabel(Map.of())
                .withByZone(Map.of())
                .build();

        assertThat(report).isEqualTo(Map.of(
                "seat", emptyReport,
                "generalAdmission", emptyReport,
                "booth", emptyReport,
                "table", tablesReport
        ));
    }

    @Test
    public void summaryByCategoryKey() {
        String chartKey = createTestChartForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByCategoryKey(chartKey, updateOptions());

        ChartReportSummaryItem cat9Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        ChartReportSummaryItem cat10Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        ChartReportSummaryItem noCategoryReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        ChartReportSummaryItem cat11Report = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();

        assertThat(report).isEqualTo(Map.of(
                "9", cat9Report,
                "10", cat10Report,
                "string11", cat11Report,
                "NO_CATEGORY", noCategoryReport)
        );
    }

    @Test
    public void summaryByCategoryLabel() {
        String chartKey = createTestChartForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByCategoryLabel(chartKey, updateOptions());

        ChartReportSummaryItem cat1Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        ChartReportSummaryItem cat2Report = aChartReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        ChartReportSummaryItem cat3Report = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        ChartReportSummaryItem noCategoryReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        ChartReportSummaryItem noZoneReport = aChartReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();

        assertThat(report).isEqualTo(Map.of(
                "Cat1", cat1Report,
                "Cat2", cat2Report,
                "Cat3", cat3Report,
                "NO_CATEGORY", noCategoryReport));
    }

    @Test
    public void summaryBySection() {
        String chartKey = createTestChartForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryBySection(chartKey, updateOptions());

        ChartReportSummaryItem noSectionReport = aChartReportSummaryItem()
                .withCount(232)
                .withByCategoryKey(Map.of("9", 116, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 116, "Cat2", 116))
                .withByObjectType(Map.of("seat", 32, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 232))
                .build();

        assertThat(report).isEqualTo(Map.of(NO_SECTION, noSectionReport));
    }

    @Test
    public void summaryByZone() {
        String chartKey = createTestChartWithZonesForReport();

        Map<String, ChartReportSummaryItem> report = client.chartReports.summaryByZone(chartKey, updateOptions());

        ChartReportSummaryItem reportForZoneFinishline = aChartReportSummaryItem()
                .withCount(2865)
                .withByCategoryKey(Map.of("1", 2865))
                .withByCategoryLabel(Map.of("Goal Stands", 2865))
                .withByObjectType(Map.of("seat", 2865))
                .withBySection(Map.of("Goal Stand 3", 2215, "Goal Stand 4", 650))
                .build();
        ChartReportSummaryItem reportForZoneMidtrack = aChartReportSummaryItem()
                .withCount(6032)
                .withByCategoryKey(Map.of("2", 6032))
                .withByCategoryLabel(Map.of("Mid Track Stand", 6032))
                .withByObjectType(Map.of("seat", 6032))
                .withBySection(Map.of("MT1", 2418, "MT3", 3614))
                .build();
        ChartReportSummaryItem reportForNoZone = aChartReportSummaryItem()
                .withCount(0)
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByObjectType(Map.of())
                .withBySection(Map.of())
                .build();

        assertThat(report).isEqualTo(Map.of(
                "midtrack", reportForZoneMidtrack,
                "finishline", reportForZoneFinishline,
                NO_ZONE, reportForNoZone
        ));
    }

    public abstract String createTestChartForReport();

    public abstract String createTestChartWithTablesForReport();

    public abstract String createTestChartWithZonesForReport();

    public abstract ChartReportOptions updateOptions();

    public abstract ChartReportOptions updateOptions(ChartReportOptions options);
}

package seatsio.reports.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectInfo.BOOKED;
import static seatsio.events.ObjectInfo.*;

public class EventReportsDeepSummaryTest extends SeatsioClientTest {

    @Test
    public void deepSummaryByStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryByStatus(event.key);

        assertThat(report.get(BOOKED).count).isEqualTo(1);
        assertThat(report.get(BOOKED).bySection.get(NO_SECTION).count).isEqualTo(1);
        assertThat(report.get(BOOKED).bySection.get(NO_SECTION).bySelectability.get(NOT_SELECTABLE)).isEqualTo(1);
    }

    @Test
    public void deepSummaryByObjectType() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryByObjectType(event.key);

        assertThat(report.get("seat").count).isEqualTo(32);
        assertThat(report.get("seat").bySection.get(NO_SECTION).count).isEqualTo(32);
        assertThat(report.get("seat").bySection.get(NO_SECTION).bySelectability.get(SELECTABLE)).isEqualTo(32);
    }

    @Test
    public void deepSummaryByCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryByCategoryKey(event.key);

        assertThat(report.get("9").count).isEqualTo(116);
        assertThat(report.get("9").bySection.get(NO_SECTION).count).isEqualTo(116);
        assertThat(report.get("9").bySection.get(NO_SECTION).bySelectability.get(NOT_SELECTABLE)).isEqualTo(1);
    }

    @Test
    public void deepSummaryByCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryByCategoryLabel(event.key);

        assertThat(report.get("Cat1").count).isEqualTo(116);
        assertThat(report.get("Cat1").bySection.get(NO_SECTION).count).isEqualTo(116);
        assertThat(report.get("Cat1").bySection.get(NO_SECTION).bySelectability.get(NOT_SELECTABLE)).isEqualTo(1);
    }

    @Test
    public void deepSummaryBySection() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryBySection(event.key);

        assertThat(report.get(NO_SECTION).count).isEqualTo(232);
        assertThat(report.get(NO_SECTION).byCategoryLabel.get("Cat1").count).isEqualTo(116);
        assertThat(report.get(NO_SECTION).byCategoryLabel.get("Cat1").bySelectability.get(NOT_SELECTABLE)).isEqualTo(1);
    }

    @Test
    public void deepSummaryBySelectability() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryBySelectability(event.key);

        assertThat(report.get(NOT_SELECTABLE).count).isEqualTo(1);
        assertThat(report.get(NOT_SELECTABLE).byCategoryLabel.get("Cat1").count).isEqualTo(1);
        assertThat(report.get(NOT_SELECTABLE).byCategoryLabel.get("Cat1").bySection.get(NO_SECTION)).isEqualTo(1);
    }

    @Test
    public void deepSummaryByChannel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1"));

        Map<String, EventReportDeepSummaryItem> report = client.eventReports.deepSummaryByChannel(event.key);

        assertThat(report.get(NO_CHANNEL).count).isEqualTo(232);
        assertThat(report.get(NO_CHANNEL).byCategoryLabel.get("Cat1").count).isEqualTo(116);
        assertThat(report.get(NO_CHANNEL).byCategoryLabel.get("Cat1").bySection.get(NO_SECTION)).isEqualTo(116);
    }

}

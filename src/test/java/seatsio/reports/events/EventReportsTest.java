package seatsio.reports.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;
import seatsio.events.Labels;
import seatsio.events.ObjectProperties;
import seatsio.holdTokens.HoldToken;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class EventReportsTest extends SeatsioClientTest {

    @Test
    public void reportItemProperties() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        Map<String, String> extraData = ImmutableMap.of("foo", "bar");
        client.events.book(event.key, asList(new ObjectProperties("A-1", "ticketType1", extraData)), null, "order1", null);

        Map<String, List<EventReportItem>> report = client.eventReports.byLabel(event.key);

        EventReportItem reportItem = report.get("A-1").get(0);
        assertThat(reportItem.status).isEqualTo("booked");
        assertThat(reportItem.label).isEqualTo("A-1");
        assertThat(reportItem.labels).isEqualTo(new Labels("1", "seat", "A", "row"));
        assertThat(reportItem.categoryLabel).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey).isEqualTo(9);
        assertThat(reportItem.ticketType).isEqualTo("ticketType1");
        assertThat(reportItem.orderId).isEqualTo("order1");
        assertThat(reportItem.forSale).isTrue();
        assertThat(reportItem.section).isNull();
        assertThat(reportItem.entrance).isNull();
        assertThat(reportItem.numBooked).isNull();
        assertThat(reportItem.capacity).isNull();
        assertThat(reportItem.objectType).isEqualTo("seat");
        assertThat(reportItem.extraData).isEqualTo(extraData);
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1"), holdToken.holdToken);

        Map<String, List<EventReportItem>> report = client.eventReports.byLabel(event.key);

        EventReportItem reportItem = report.get("A-1").get(0);
        assertThat(reportItem.holdToken).isEqualTo(holdToken.holdToken);
    }

    @Test
    public void reportItemPropertiesForGA() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList(new ObjectProperties("GA1", 5)));
        String holdToken = client.holdTokens.create().holdToken;
        client.events.hold(event.key, asList(new ObjectProperties("GA1", 3)), holdToken);

        Map<String, List<EventReportItem>> report = client.eventReports.byLabel(event.key);

        EventReportItem reportItem = report.get("GA1").get(0);
        assertThat(reportItem.numBooked).isEqualTo(5);
        assertThat(reportItem.numFree).isEqualTo(92);
        assertThat(reportItem.numHeld).isEqualTo(3);
        assertThat(reportItem.capacity).isEqualTo(100);
        assertThat(reportItem.objectType).isEqualTo("generalAdmission");
    }

    @Test
    public void byStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(event.key, asList("A-1", "A-2"), "lolzor");
        client.events.changeObjectStatus(event.key, asList("A-3"), "booked");

        Map<String, List<EventReportItem>> report = client.eventReports.byStatus(event.key);

        assertThat(report.get("lolzor")).hasSize(2);
        assertThat(report.get("booked")).hasSize(1);
        assertThat(report.get("free")).hasSize(31);
    }

    @Test
    public void byStatus_emptyChart() {
        String chartKey = client.charts.create().key;
        Event event = client.events.create(chartKey);

        Map<String, List<EventReportItem>> report = client.eventReports.byStatus(event.key);

        assertThat(report).isEmpty();
    }

    @Test
    public void bySpecificStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(event.key, asList("A-1", "A-2"), "lolzor");
        client.events.changeObjectStatus(event.key, asList("A-3"), "booked");

        List<EventReportItem> report = client.eventReports.byStatus(event.key, "lolzor");

        assertThat(report).hasSize(2);
    }

    @Test
    public void bySpecificNonExistingStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        List<EventReportItem> report = client.eventReports.byStatus(event.key, "lolzor");

        assertThat(report).isEmpty();
    }

    @Test
    public void byCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, List<EventReportItem>> report = client.eventReports.byCategoryLabel(event.key);

        assertThat(report.get("Cat1")).hasSize(17);
        assertThat(report.get("Cat2")).hasSize(17);
    }

    @Test
    public void bySpecificCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        List<EventReportItem> report = client.eventReports.byCategoryLabel(event.key, "Cat1");

        assertThat(report).hasSize(17);
    }

    @Test
    public void byCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, List<EventReportItem>> report = client.eventReports.byCategoryKey(event.key);

        assertThat(report.get("9")).hasSize(17);
        assertThat(report.get("10")).hasSize(17);
    }

    @Test
    public void bySpecificCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        List<EventReportItem> report = client.eventReports.byCategoryKey(event.key, "9");

        assertThat(report).hasSize(17);
    }

    @Test
    public void byLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, List<EventReportItem>> report = client.eventReports.byLabel(event.key);

        assertThat(report.get("A-1")).hasSize(1);
        assertThat(report.get("A-2")).hasSize(1);
    }

    @Test
    public void bySpecificLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        List<EventReportItem> report = client.eventReports.byLabel(event.key, "A-1");

        assertThat(report).hasSize(1);
    }

    @Test
    public void byOrderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList("A-1", "A-2"), null, "order1", null);
        client.events.book(event.key, asList("A-3"), null, "order2", null);

        Map<String, List<EventReportItem>> report = client.eventReports.byOrderId(event.key);

        assertThat(report.get("order1")).hasSize(2);
        assertThat(report.get("order2")).hasSize(1);
        assertThat(report.get("NO_ORDER_ID")).hasSize(31);
    }

    @Test
    public void bySpecificOrderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList("A-1", "A-2"), null, "order1", null);
        client.events.book(event.key, asList("A-3"), null, "order2", null);

        List<EventReportItem> report = client.eventReports.byOrderId(event.key, "order1");

        assertThat(report).hasSize(2);
    }

    @Test
    public void bySection() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, List<EventReportItem>> report = client.eventReports.bySection(event.key);

        assertThat(report.get("NO_SECTION")).hasSize(34);
    }

    @Test
    public void bySpecificSection() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        List<EventReportItem> report = client.eventReports.bySection(event.key, "NO_SECTION");

        assertThat(report).hasSize(34);
    }
}

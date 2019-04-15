package seatsio.reports.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;
import seatsio.events.ObjectProperties;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.reports.events.EventReportSummaryItemBuilder.anEventReportSummaryItem;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class EventReportsSummaryTest extends SeatsioClientTest {

    @Test
    public void summaryByStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByStatus(event.key);

        EventReportSummaryItem bookedReport = anEventReportSummaryItem()
                .withCount(1)
                .withBySection(ImmutableMap.of("NO_SECTION", 1))
                .withByCategoryKey(ImmutableMap.of("9", 1))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 1))
                .build();
        EventReportSummaryItem freeReport = anEventReportSummaryItem()
                .withCount(231)
                .withBySection(ImmutableMap.of("NO_SECTION", 231))
                .withByCategoryKey(ImmutableMap.of("9", 115, "10", 116))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 115, "Cat2", 116))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of(BOOKED, bookedReport, FREE, freeReport));
    }

    @Test
    public void summaryByCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByCategoryKey(event.key);

        EventReportSummaryItem cat9Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of("NO_SECTION", 116))
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 115))
                .build();
        EventReportSummaryItem cat10Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of("NO_SECTION", 116))
                .withByStatus(ImmutableMap.of(FREE, 116))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("9", cat9Report, "10", cat10Report));
    }

    @Test
    public void summaryByCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByCategoryLabel(event.key);

        EventReportSummaryItem cat1Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of("NO_SECTION", 116))
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 115))
                .build();
        EventReportSummaryItem cat2Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(ImmutableMap.of("NO_SECTION", 116))
                .withByStatus(ImmutableMap.of(FREE, 116))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("Cat1", cat1Report, "Cat2", cat2Report));
    }

    @Test
    public void summaryBySection() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryBySection(event.key);

        EventReportSummaryItem noSectionReport = anEventReportSummaryItem()
                .withCount(232)
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 231))
                .withByCategoryKey(ImmutableMap.of("9", 116, "10", 116))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 116, "Cat2", 116))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("NO_SECTION", noSectionReport));
    }

}

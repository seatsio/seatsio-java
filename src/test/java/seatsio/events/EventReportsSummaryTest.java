package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventSummaryReportItemBuilder.anEventSummaryReportItem;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class EventReportsSummaryTest extends SeatsioClientTest {

    @Test
    public void summaryByStatus() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        client.events().book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventSummaryReportItem> report = client.events().reports().summaryByStatus(event.key);

        EventSummaryReportItem bookedReport = anEventSummaryReportItem()
                .withCount(1)
                .withBySection(ImmutableMap.of("NO_SECTION", 1))
                .withByCategoryKey(ImmutableMap.of("9", 1))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 1))
                .build();
        EventSummaryReportItem freeReport = anEventSummaryReportItem()
                .withCount(33)
                .withBySection(ImmutableMap.of("NO_SECTION", 33))
                .withByCategoryKey(ImmutableMap.of("9", 16, "10", 17))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 16, "Cat2", 17))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of(BOOKED, bookedReport, FREE, freeReport));
    }

    @Test
    public void summaryByCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        client.events().book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventSummaryReportItem> report = client.events().reports().summaryByCategoryKey(event.key);

        EventSummaryReportItem cat9Report = anEventSummaryReportItem()
                .withCount(17)
                .withBySection(ImmutableMap.of("NO_SECTION", 17))
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 16))
                .build();
        EventSummaryReportItem cat10Report = anEventSummaryReportItem()
                .withCount(17)
                .withBySection(ImmutableMap.of("NO_SECTION", 17))
                .withByStatus(ImmutableMap.of(FREE, 17))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("9", cat9Report, "10", cat10Report));
    }

    @Test
    public void summaryByCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        client.events().book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventSummaryReportItem> report = client.events().reports().summaryByCategoryLabel(event.key);

        EventSummaryReportItem cat1Report = anEventSummaryReportItem()
                .withCount(17)
                .withBySection(ImmutableMap.of("NO_SECTION", 17))
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 16))
                .build();
        EventSummaryReportItem cat2Report = anEventSummaryReportItem()
                .withCount(17)
                .withBySection(ImmutableMap.of("NO_SECTION", 17))
                .withByStatus(ImmutableMap.of(FREE, 17))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("Cat1", cat1Report, "Cat2", cat2Report));
    }

    @Test
    public void summaryBySection() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        client.events().book(event.key, asList(new ObjectProperties("A-1", "ticketType1")), null, "order1");

        Map<String, EventSummaryReportItem> report = client.events().reports().summaryBySection(event.key);

        EventSummaryReportItem noSectionReport = anEventSummaryReportItem()
                .withCount(34)
                .withByStatus(ImmutableMap.of(BOOKED, 1, FREE, 33))
                .withByCategoryKey(ImmutableMap.of("9", 17, "10", 17))
                .withByCategoryLabel(ImmutableMap.of("Cat1", 17, "Cat2", 17))
                .build();
        assertThat(report).isEqualTo(ImmutableMap.of("NO_SECTION", noSectionReport));
    }

}

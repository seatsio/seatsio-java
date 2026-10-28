package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;
import seatsio.util.Page;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListEventsOfChartTest extends SeatsioClientTest {

    @Test
    public void all() {
        Chart chart = client.charts.create();
        Chart otherChart = client.charts.create();
        Event event1 = client.events.create(chart.key());
        Event event2 = client.events.create(chart.key());
        client.events.create(otherChart.key());
        Event event3 = client.events.create(chart.key());

        Stream<Event> events = client.charts.events(chart.key()).all();

        assertThat(events)
                .extracting(Event::key)
                .containsExactly(event3.key(), event2.key(), event1.key());
    }

    @Test
    public void pages() {
        Chart chart = client.charts.create();
        Event event1 = client.events.create(chart.key());
        Event event2 = client.events.create(chart.key());
        Event event3 = client.events.create(chart.key());

        Page<Event> firstPage = client.charts.events(chart.key()).firstPage(2);
        Page<Event> pageAfter = client.charts.events(chart.key()).pageAfter(firstPage.nextPageStartsAfter.get(), 2);

        assertThat(firstPage.items)
                .extracting(Event::key)
                .containsExactly(event3.key(), event2.key());
        assertThat(pageAfter.items)
                .extracting(Event::key)
                .containsExactly(event1.key());
    }

}

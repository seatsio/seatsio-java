package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListEventsTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Event event1 = client.events.create(chart.key);
        Event event2 = client.events.create(chart.key);
        Event event3 = client.events.create(chart.key);

        Stream<Event> events = client.events.listAll();

        assertThat(events)
                .extracting(event -> event.key)
                .containsExactly(event3.key, event2.key, event1.key);
    }

}

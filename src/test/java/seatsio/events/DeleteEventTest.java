package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.Chart;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteEventTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.delete(event.key);

        assertThrows(SeatsioException.class, () -> client.events.retrieve(event.key));
    }

}

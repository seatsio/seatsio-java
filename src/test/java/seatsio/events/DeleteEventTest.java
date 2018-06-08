package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.Chart;

public class DeleteEventTest extends SeatsioClientTest {

    @Test(expected = SeatsioException.class)
    public void test() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.delete(event.key);

        client.events.retrieve(event.key);
    }

}

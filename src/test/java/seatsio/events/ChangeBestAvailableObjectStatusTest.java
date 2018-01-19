package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeBestAvailableObjectStatusTest extends SeatsioClientTest {

    @Test
    public void number() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        BestAvailableResult bestAvailableResult = client.events().changeObjectStatus(event.key, new BestAvailable(3), "foo");

        assertThat(bestAvailableResult.nextToEachOther).isTrue();
        assertThat(bestAvailableResult.objects).containsOnly("B-3", "B-4", "B-5");
    }

    @Test
    public void categories() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        BestAvailableResult bestAvailableResult = client.events().changeObjectStatus(event.key, new BestAvailable(3, asList("cat2")), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("C-3", "C-4", "C-5");
    }

}

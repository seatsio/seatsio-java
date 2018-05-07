package seatsio.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeBestAvailableObjectStatusTest extends SeatsioClientTest {

    @Test
    public void number() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(3), "foo");

        assertThat(bestAvailableResult.nextToEachOther).isTrue();
        assertThat(bestAvailableResult.objects).containsOnly("B-4", "B-5", "B-6");
    }

    @Test
    public void categories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(3, asList("cat2")), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("C-4", "C-5", "C-6");
    }

    @Test
    public void extraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        List<Map<String, Object>> extraData = newArrayList(
                ImmutableMap.of("foo", "bar"),
                ImmutableMap.of("foo", "baz")
        );

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(2, null, extraData), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("B-4", "B-5");
        assertThat(client.events.retrieveObjectStatus(event.key, "B-4").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
        assertThat(client.events.retrieveObjectStatus(event.key, "B-5").extraData).isEqualTo(ImmutableMap.of("foo", "baz"));
    }

}

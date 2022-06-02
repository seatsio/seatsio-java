package seatsio.events;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.HashSet;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class AddChannelTest extends SeatsioClientTest {

    @Test
    public void addChannel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.addChannel(event.key, "channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2"));
        client.events.addChannel(event.key, "channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"))
        );
    }

    @Test
    public void indexIsOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.addChannel(event.key, "channelKey1", "channel 1", "#FFFF98", null, newHashSet("A-1", "A-2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 0, newHashSet("A-1", "A-2"))
        );
    }

    @Test
    public void objectsAreOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.addChannel(event.key, "channelKey1", "channel 1", "#FFFF98", 1, null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet())
        );
    }

}

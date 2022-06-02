package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveChannelTest extends SeatsioClientTest {

    @Test
    public void removeChannel() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1),
                "channelKey2", new Channel("channel 2", "#FFFF99", 2)
        ));

        client.events.removeChannel(event.key, "channelKey2");

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1)
        );
    }

}

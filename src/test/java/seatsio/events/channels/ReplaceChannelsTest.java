package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Channel;
import seatsio.events.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ReplaceChannelsTest extends SeatsioClientTest {

    @Test
    public void replaceChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.replace(event.key(), List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        ));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        );
    }

    @Test
    public void replaceChannelsWithOnlyRequiredFields() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key());

        client.events.channels.replace(event.key(), List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", null, null),
                new Channel("channelKey2", "channel 2", "#FFFF99", null, null)
        ));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", null, new HashSet<>()),
                new Channel("channelKey2", "channel 2", "#FFFF99", null, new HashSet<>())
        );
    }

}

package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Channel;
import seatsio.events.CreateEventParams;
import seatsio.events.Event;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveChannelTest extends SeatsioClientTest {

    @Test
    public void removeChannel() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key(), new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, null),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, null)
        )));

        client.events.channels.remove(event.key(), "channelKey2");

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, new HashSet<>())
        );
    }

}

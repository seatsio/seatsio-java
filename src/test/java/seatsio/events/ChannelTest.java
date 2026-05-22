package seatsio.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChannelTest {

    @Test
    public void areaPartitionLabel() {
        Channel channel = new Channel("key", "ch_abc123", "My Channel", "#FF0000", 0, null, null);
        assertThat(channel.areaPartitionLabel("GA1")).isEqualTo("GA1##ch_abc123");
    }
}


package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class AddEventsToPartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, "aSeason", newArrayList("event1", "event2"), null, null);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", null);

        Season updatedPartialSeason = client.seasons.addEventsToPartialSeason("aSeason", "aPartialSeason", newArrayList("event1", "event2"));

        assertThat(updatedPartialSeason.events)
                .extracting(s -> s.key)
                .containsExactly("event1", "event2");
    }
}

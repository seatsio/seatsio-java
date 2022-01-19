package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveEventFromPartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, "aSeason", newArrayList("event1", "event2"), null, null);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", newArrayList("event1", "event2"));

        Season updatedPartialSeason = client.seasons.removeEventFromPartialSeason("aSeason", "aPartialSeason", "event2");

        assertThat(updatedPartialSeason.events)
                .extracting(s -> s.key)
                .containsExactly("event1");
        assertThat(client.seasons.retrieve("aSeason").events)
                .extracting(s -> s.key)
                .containsExactly("event1", "event2");
    }
}

package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveEventFromPartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        SeasonParams seasonParams = new SeasonParams()
                .key("aSeason")
                .eventKeys(newArrayList("event1", "event2"));
        client.seasons.create(chartKey, seasonParams);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", newArrayList("event1", "event2"));

        Season updatedPartialSeason = client.seasons.removeEventFromPartialSeason("aSeason", "aPartialSeason", "event2");

        assertThat(updatedPartialSeason.events)
                .extracting(s -> s.key)
                .containsExactly("event1");
        assertThat(client.events.retrieveSeason("aSeason").events)
                .extracting(s -> s.key)
                .containsExactly("event1", "event2");
    }
}

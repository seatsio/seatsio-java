package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveEventFromPartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        SeasonParams seasonParams = new SeasonParams()
                .key("aSeason")
                .eventKeys(List.of("event1", "event2"));
        client.seasons.create(chartKey, seasonParams);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", List.of("event1", "event2"));

        Season updatedPartialSeason = client.seasons.removeEventFromPartialSeason("aSeason", "aPartialSeason", "event2");

        assertThat(updatedPartialSeason.events)
                .extracting(s -> s.key)
                .containsExactly("event1");
        assertThat(client.seasons.retrieve("aSeason").events)
                .extracting(s -> s.key)
                .containsExactly("event1", "event2");
    }
}

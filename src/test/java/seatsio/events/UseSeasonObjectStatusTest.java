package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.seasons.CreateSeasonParams;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.events.EventObjectInfo.BOOKED;

public class UseSeasonObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new CreateSeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.events.book("aSeason", List.of("A-1"));
        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"));

        client.events.useSeasonObjectStatus("event1", List.of("A-1"));

        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status()).isEqualTo(BOOKED);
    }

    @Test
    public void test_partialSeason() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new CreateSeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", null, List.of("event1"));
        client.events.book("aPartialSeason", List.of("A-1"));
        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"), "aPartialSeason");

        client.events.useSeasonObjectStatus("event1", List.of("A-1"), "aPartialSeason");

        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status()).isEqualTo(BOOKED);
    }

    @Test
    public void test_partialSeason_invalid() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new CreateSeasonParams().key("aSeason").eventKeys(List.of("event1", "event2")));
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", null, List.of("event1"));
        client.events.book("aPartialSeason", List.of("A-1"));
        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"), "aPartialSeason");

        assertThrows(SeatsioException.class, () -> client.events.useSeasonObjectStatus("event2", List.of("A-1"), "aPartialSeason"));
    }
}

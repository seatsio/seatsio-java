package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.seasons.CreateSeasonParams;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.FREE;

public class OverrideSeasonObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new CreateSeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.events.book("aSeason", List.of("A-1"));

        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"));

        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status()).isEqualTo(FREE);
    }
}

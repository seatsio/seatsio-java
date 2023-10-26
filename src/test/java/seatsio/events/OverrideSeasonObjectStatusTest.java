package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.seasons.SeasonParams;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.FREE;

public class OverrideSeasonObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new SeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.events.book("aSeason", List.of("A-1"));

        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"));

        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status).isEqualTo(FREE);
    }
}

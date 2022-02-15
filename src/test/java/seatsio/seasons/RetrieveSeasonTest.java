package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;
import seatsio.events.TableBookingConfig;

import java.time.Instant;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveSeasonTest extends SeatsioClientTest {

    @Test
    public void retrieveSeason() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);
        client.seasons.createEvents(season.key, newArrayList("event1", "event2"));
        Season partialSeason1 = client.seasons.createPartialSeason(season.key, null, null);
        Season partialSeason2 = client.seasons.createPartialSeason(season.key, null, null);

        Season retrievedSeason = client.seasons.retrieve(season.key);

        assertThat(retrievedSeason.id).isNotZero();
        assertThat(retrievedSeason.key).isEqualTo(season.key);
        assertThat(retrievedSeason.chartKey).isEqualTo(chartKey);
        assertThat(retrievedSeason.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(retrievedSeason.forSaleConfig).isNull();
        assertThat(retrievedSeason.supportsBestAvailable).isTrue();
        Instant now = Instant.now();
        assertThat(retrievedSeason.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(retrievedSeason.updatedOn).isNull();
        assertThat(retrievedSeason.partialSeasonKeys).containsExactly(partialSeason1.key, partialSeason2.key);
        assertThat(retrievedSeason.events)
                .extracting("key")
                .containsExactly("event1", "event2");
    }
}

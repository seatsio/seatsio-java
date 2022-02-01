package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.TableBookingConfig;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);
        Season partialSeason1 = client.seasons.createPartialSeason(season.key, null, null);
        Season partialSeason2 = client.seasons.createPartialSeason(season.key, null, null);

        Season retrievedSeason = client.seasons.retrieve(season.key);

        assertThat(retrievedSeason.id).isNotZero();
        assertThat(retrievedSeason.key).isEqualTo(season.key);
        assertThat(retrievedSeason.partialSeasonKeys).containsExactly(partialSeason1.key, partialSeason2.key);

        assertThat(retrievedSeason.seasonEvent.id).isNotZero();
        assertThat(retrievedSeason.seasonEvent.key).isEqualTo(season.key);
        assertThat(retrievedSeason.seasonEvent.chartKey).isEqualTo(chartKey);
        assertThat(retrievedSeason.seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(retrievedSeason.seasonEvent.forSaleConfig).isNull();
        assertThat(retrievedSeason.seasonEvent.supportsBestAvailable).isTrue();
        Instant now = Instant.now();
        assertThat(retrievedSeason.seasonEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(retrievedSeason.seasonEvent.updatedOn).isNull();
    }
}

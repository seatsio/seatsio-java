package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.TableBookingConfig;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrievePartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, "aSeason", null, null, null);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", null);

        Season retrievedPartialSeason = client.seasons.retrievePartialSeason("aSeason", "aPartialSeason");

        assertThat(retrievedPartialSeason.id).isNotZero();
        assertThat(retrievedPartialSeason.key).isEqualTo("aPartialSeason");

        assertThat(retrievedPartialSeason.seasonEvent.id).isNotZero();
        assertThat(retrievedPartialSeason.seasonEvent.key).isEqualTo("aPartialSeason");
        assertThat(retrievedPartialSeason.seasonEvent.chartKey).isEqualTo(chartKey);
        assertThat(retrievedPartialSeason.seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(retrievedPartialSeason.seasonEvent.forSaleConfig).isNull();
        assertThat(retrievedPartialSeason.seasonEvent.supportsBestAvailable).isTrue();
        Instant now = Instant.now();
        assertThat(retrievedPartialSeason.seasonEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(retrievedPartialSeason.seasonEvent.updatedOn).isNull();
    }
}

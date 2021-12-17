package seatsio.seasons;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;
import seatsio.events.Event;
import seatsio.events.TableBookingConfig;

import java.time.Instant;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateSeasonTest extends SeatsioClientTest {

    @Test
    public void chartKeyIsRequired() {
        String chartKey = createTestChart();

        Season season = client.seasons.create(chartKey);

        assertThat(season.id).isNotZero();
        assertThat(season.key).isNotNull();

        Event seasonEvent = season.seasonEvent;
        assertThat(seasonEvent.chartKey).isEqualTo(chartKey);
        assertThat(seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(seasonEvent.supportsBestAvailable).isTrue();
        assertThat(seasonEvent.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(seasonEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(seasonEvent.updatedOn).isNull();

        assertThat(season.events).isEmpty();
    }

    @Test
    public void keyCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, "aSeason", 0);

        assertThat(season.key).isEqualTo("aSeason");
    }

    @Test
    public void numberOfEventsCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, "aSeason", 2);

        assertThat(season.events).hasSize(2);
    }

    @Test
    public void eventKeysCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, "aSeason", newArrayList("event1", "event2"));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event2");
    }
}

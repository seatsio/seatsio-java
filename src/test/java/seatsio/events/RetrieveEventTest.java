package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.seasons.Season;

import java.time.Instant;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveEventTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Event retrievedEvent = client.events.retrieve(event.key);

        assertThat(retrievedEvent.id).isNotZero();
        assertThat(retrievedEvent.isEventInSeason).isFalse();
        assertThat(retrievedEvent.key).isNotNull();
        assertThat(retrievedEvent.chartKey).isEqualTo(chartKey);
        assertThat(retrievedEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(retrievedEvent.forSaleConfig).isNull();
        assertThat(retrievedEvent.supportsBestAvailable).isTrue();
        Instant now = Instant.now();
        assertThat(retrievedEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(retrievedEvent.updatedOn).isNull();
        assertThat(retrievedEvent.categories).containsExactly(
                new Category(9L, "Cat1", "#87A9CD", false),
                new Category(10L, "Cat2", "#5E42ED", false),
                new Category("string11", "Cat3", "#5E42BB", false)
        );
    }

    @Test
    public void retrieveSeason() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);
        client.seasons.createEvents(season.key, newArrayList("event1", "event2"));
        Season partialSeason1 = client.seasons.createPartialSeason(season.key, null, null);
        Season partialSeason2 = client.seasons.createPartialSeason(season.key, null, null);

        Season retrievedSeason = (Season) client.events.retrieve(season.key);

        assertThat(retrievedSeason.id).isNotZero();
        assertThat(retrievedSeason.isTopLevelSeason).isTrue();
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
        assertThat(retrievedSeason.categories).containsExactly(
                new Category(9L, "Cat1", "#87A9CD", false),
                new Category(10L, "Cat2", "#5E42ED", false),
                new Category("string11", "Cat3", "#5E42BB", false)
        );
    }
}

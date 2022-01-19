package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListSeasonsTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Season season1 = client.seasons.create(chart.key);
        Season season2 = client.seasons.create(chart.key);
        Season season3 = client.seasons.create(chart.key);

        Stream<Season> seasons = client.seasons.listAll();

        assertThat(seasons)
                .extracting(season -> season.key)
                .containsExactly(season3.key, season2.key, season1.key);
    }
}

package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        client.seasons.delete(season.key);

        assertThrows(SeatsioException.class, () -> client.seasons.retrieve(season.key));
    }
}

package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeletePartialSeasonTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, "aSeason", null, null, null);
        client.seasons.createPartialSeason("aSeason", "aPartialSeason", null);

        client.seasons.deletePartialSeason("aSeason", "aPartialSeason");

        assertThrows(SeatsioException.class, () -> client.seasons.retrievePartialSeason("aSeason", "aPartialSeason"));
    }
}

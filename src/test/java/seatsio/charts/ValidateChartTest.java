package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ValidateChartTest extends SeatsioClientTest {

    @Test
    public void validatePublishedChart() {
        String chartKey = createTestChartWithErrors();

        ChartValidationResult validationRes = client.charts.validatePublishedVersion(chartKey);

        assertThat(validationRes.errors).isEmpty();
    }

    @Test
    public void validateDraftChart() {
        String chartKey = createTestChartWithErrors();

        assertThrows(SeatsioException.class, () -> client.charts.validateDraftVersion(chartKey));
    }
}

package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import static org.assertj.core.api.Assertions.assertThat;


public class ValidateChartTest extends SeatsioClientTest {

    @Test
    public void validatePublishedChart() {
        String chartKey = createTestChartWithErrors();

        ChartValidation validationRes = client.charts.validatePublishedVersion(chartKey);

        assertThat(validationRes.errors).contains("VALIDATE_DUPLICATE_LABELS");
        assertThat(validationRes.errors).contains("VALIDATE_UNLABELED_OBJECTS");
        assertThat(validationRes.errors).contains("VALIDATE_UNLABELED_OBJECTS");
    }

    @Test(expected = seatsio.SeatsioException.class)
    public void validateDraftChart() {
        String chartKey = createTestChartWithErrors();


        client.events.create(chartKey);

        client.charts.update(chartKey, "New name");

        client.charts.validateDraftVersion(chartKey);
    }
}

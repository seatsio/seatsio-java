package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import static org.assertj.core.api.Assertions.assertThat;


public class ValidateChartTest extends SeatsioClientTest {

    @Test
    public void validatePublishedChart() {
        String chartKey = createTestChartWithErrors();

        ChartValidation validationRes = client.charts.validatePublishedVersion(chartKey);

        ChartValidationChildren children = new ChartValidationChildren();
        children.level = "ERROR";
        children.validatorKey = "VALIDATE_DUPLICATE_LABELS";

        assertThat(validationRes.errors).contains(children);

        ChartValidationChildren children2 = new ChartValidationChildren();
        children2.level = "ERROR";
        children2.validatorKey = "VALIDATE_UNLABELED_OBJECTS";

        assertThat(validationRes.errors).contains(children2);

        ChartValidationChildren children3 = new ChartValidationChildren();
        children3.level = "ERROR";
        children3.validatorKey = "VALIDATE_OBJECTS_WITHOUT_CATEGORIES";

        assertThat(validationRes.errors).contains(children3);
    }

    @Test(expected = seatsio.SeatsioException.class)
    public void validateDraftChart() {
        String chartKey = createTestChartWithErrors();


        client.events.create(chartKey);

        client.charts.update(chartKey, "New name");

        client.charts.validateDraftVersion(chartKey);
    }
}

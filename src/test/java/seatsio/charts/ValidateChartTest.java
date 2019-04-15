package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import static org.assertj.core.api.Assertions.assertThat;


public class ValidateChartTest extends SeatsioClientTest {

    @Test
    public void validatePublishedChart() {
        String chartKey = createTestChartWithErrors();

        ChartValidation validationRes = client.charts.validatePublishedVersion(chartKey);

        ChartValidationItem item = new ChartValidationItem();
        item.level = "ERROR";
        item.validatorKey = "VALIDATE_DUPLICATE_LABELS";

        assertThat(validationRes.errors).contains(item);

        ChartValidationItem item2 = new ChartValidationItem();
        item2.level = "ERROR";
        item2.validatorKey = "VALIDATE_UNLABELED_OBJECTS";

        assertThat(validationRes.errors).contains(item2);

        ChartValidationItem item3 = new ChartValidationItem();
        item3.level = "ERROR";
        item3.validatorKey = "VALIDATE_OBJECTS_WITHOUT_CATEGORIES";

        assertThat(validationRes.errors).contains(item3);
    }

    @Test(expected = seatsio.SeatsioException.class)
    public void validateDraftChart() {
        String chartKey = createTestChartWithErrors();


        client.events.create(chartKey);

        client.charts.update(chartKey, "New name");

        client.charts.validateDraftVersion(chartKey);
    }
}

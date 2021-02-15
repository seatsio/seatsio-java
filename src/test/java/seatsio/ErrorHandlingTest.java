package seatsio;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorHandlingTest extends SeatsioClientTest {

    @Test
    public void test400() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> client.charts.retrieve("unexistingChart"));
        assertThat(e.getMessage()).contains("GET " + client.getBaseUrl() + "/charts/unexistingChart resulted in a 404 Not Found response. Reason: Chart not found: unexistingChart. Request ID:");
        assertThat(e.errors).containsExactly(new ApiError("CHART_NOT_FOUND", "Chart not found: unexistingChart"));
    }

    @Test
    public void testWeirdError() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> new SeatsioClient("", null, "unknownProtocol://").charts.retrieve("unexistingChart"));
        assertThat(e.getMessage()).contains("Error while executing GET unknownProtocol:///charts/unexistingChart");
        assertThat(e.errors).isNull();
    }
}

package seatsio;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlingTest extends SeatsioClientTest {

    @Test(expected = SeatsioException.class)
    public void test400() {
        try {
            client.charts().retrieve("unexistingChart");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).contains("GET " + client.getBaseUrl() + "/charts/unexistingChart resulted in a 404 Not Found response. Reason: Chart not found: unexistingChart. Request ID:");
            assertThat(e.messages).containsExactly("Chart not found: unexistingChart");
            assertThat(e.requestId).isNotNull();
            throw e;
        }
    }

    @Test(expected = SeatsioException.class)
    public void testWeirdError() {
        try {
            new SeatsioClient("", "unknownProtocol://").charts().retrieve("unexistingChart");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).contains("Error while executing GET unknownProtocol:///charts/unexistingChart");
            assertThat(e.messages).isNull();
            assertThat(e.requestId).isNull();
            throw e;
        }
    }
}

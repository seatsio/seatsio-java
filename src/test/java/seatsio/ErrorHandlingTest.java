package seatsio;

import org.junit.jupiter.api.Test;
import seatsio.util.UnirestWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.util.UnirestWrapper.get;

public class ErrorHandlingTest extends SeatsioClientTest {

    @Test
    public void test400() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> client.charts.retrieve("unexistingChart"));
        assertThat(e.getMessage()).contains("Chart not found: unexistingChart");
        assertThat(e.errors).containsExactly(new ApiError("CHART_NOT_FOUND", "Chart not found: unexistingChart"));
    }

    @Test
    public void test500() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.seatsio.net/status/500")));
        assertThat(e.getMessage()).isEqualTo("GET https://httpbin.seatsio.net/status/500 resulted in a 500 Internal Server Error response. Body: ");
        assertThat(e.errors).isNull();
    }

    @Test
    public void testWeirdError() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> new SeatsioClient("", null, "unknownProtocol://").charts.retrieve("unexistingChart"));
        assertThat(e.getMessage()).contains("Error while executing GET unknownProtocol:///charts/unexistingChart");
        assertThat(e.errors).isNull();
    }
}

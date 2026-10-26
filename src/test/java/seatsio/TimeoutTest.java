package seatsio;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TimeoutTest {

    private HttpServer server;
    private final AtomicInteger requestCount = new AtomicInteger();

    @BeforeEach
    public void startSlowServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            requestCount.incrementAndGet();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    @Test
    public void socketTimeoutIsConfigurableAndTimedOutRequestsAreNotRetried() {
        SeatsioClient client = new SeatsioClient("secretKey", null, "http://localhost:" + server.getAddress().getPort())
                .socketTimeout(Duration.ofMillis(300));

        Instant start = Instant.now();
        assertThatThrownBy(() -> client.charts.retrieve("someChart")).isInstanceOf(SeatsioException.class);

        assertThat(Duration.between(start, Instant.now())).isLessThan(Duration.ofSeconds(2));
        assertThat(requestCount.get()).isEqualTo(1);
    }
}

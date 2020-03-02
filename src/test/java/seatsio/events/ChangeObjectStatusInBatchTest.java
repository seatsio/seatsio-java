package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeObjectStatusInBatchTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey1 = createTestChart();
        String chartKey2 = createTestChart();
        Event event1 = client.events.create(chartKey1);
        Event event2 = client.events.create(chartKey2);

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event1.key, newArrayList("A-1"), "lolzor"),
                new StatusChangeRequest(event2.key, newArrayList("A-2"), "lolzor")
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectStatus(event1.key, "A-1").status).isEqualTo("lolzor");

        assertThat(result.get(1).objects.get("A-2").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectStatus(event2.key, "A-2").status).isEqualTo("lolzor");
    }

}

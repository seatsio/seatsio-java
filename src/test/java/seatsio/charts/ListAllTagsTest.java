package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListAllTagsTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart1 = client.charts.create();
        client.charts.addTag(chart1.key(), "tag1");
        client.charts.addTag(chart1.key(), "tag2");

        Chart chart2 = client.charts.create();
        client.charts.addTag(chart2.key(), "tag3");

        List<String> tags = client.charts.listAllTags();
        assertThat(tags).containsOnly("tag1", "tag2", "tag3");
    }

}

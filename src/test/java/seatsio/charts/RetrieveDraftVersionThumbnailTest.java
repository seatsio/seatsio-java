package seatsio.charts;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDraftVersionThumbnailTest extends SeatsioClientTest {

    @Test
    public void test() throws IOException {
        Chart chart = client.charts().create();
        client.events().create(chart.key);
        client.charts().update(chart.key, "newName");

        InputStream thumbnail = client.charts().retrieveDraftVersionThumbnail(chart.key);

        assertThat(IOUtils.toString(thumbnail, "UTF-8")).contains("<!DOCTYPE svg");
    }
}

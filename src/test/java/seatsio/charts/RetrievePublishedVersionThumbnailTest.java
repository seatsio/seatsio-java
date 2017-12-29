package seatsio.charts;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrievePublishedVersionThumbnailTest extends SeatsioClientTest {

    @Test
    public void test() throws IOException {
        Chart chart = client.charts().create();

        InputStream thumbnail = client.charts().retrievePublishedVersionThumbnail(chart.key);

        assertThat(IOUtils.toString(thumbnail, "UTF-8")).contains("<!DOCTYPE svg");
    }
}

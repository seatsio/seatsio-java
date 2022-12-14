package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.fail;

public class ManageCategoriesTest extends SeatsioClientTest {

    @Test
    public void addCategory() {
        Chart chart = client.charts.create("aChart", "BOOTHS", null);

        try {
            client.charts.addCategory(chart.key, new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa", true));
        } catch (SeatsioException e) {
            fail("did not expect an exception", e);
        }
    }

    @Test
    public void removeCategory() {
        List<Category> categories = newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "BOOTHS", categories);

        try {
            client.charts.removeCategory(chart.key, CategoryKey.of(1L));
        } catch (SeatsioException e) {
            fail("did not expect an exception", e);
        }


    }

}

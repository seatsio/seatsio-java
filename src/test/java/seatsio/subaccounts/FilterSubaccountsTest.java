package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.util.Page;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterSubaccountsTest extends SeatsioClientTest {

    @Test
    public void filter() {
        Subaccount subaccount1 = client.subaccounts.create("test1");
        Subaccount subaccount2 = client.subaccounts.create("test2");
        Subaccount subaccount3 = client.subaccounts.create("test3");

        Stream<Subaccount> subaccounts = client.subaccounts.listAll("test2");

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
    }

    @Test
    public void filterWithSpecialChars() {
        Subaccount subaccount1 = client.subaccounts.create("test-/@/1");
        Subaccount subaccount2 = client.subaccounts.create("test-/@/2");
        Subaccount subaccount3 = client.subaccounts.create("test-/@/3");

        Stream<Subaccount> subaccounts = client.subaccounts.listAll("test-/@/2");

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
    }

    @Test
    public void filterWithNoResults() {
        Stream<Subaccount> subaccounts = client.subaccounts.listAll("test2");

        assertThat(subaccounts.toArray())
                .isEmpty();
    }

    @Test
    public void filterAllOnFirstPage() {
        Subaccount subaccount1 = client.subaccounts.create("test-/@/1");
        Subaccount subaccount2 = client.subaccounts.create("test-/@/2");
        Subaccount subaccount3 = client.subaccounts.create("test-/@/3");

        Page<Subaccount> subaccounts = client.subaccounts.listFirstPage(null, "test-/@/2");

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).isNotPresent();
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }

    @Test
    public void filterWithPreviousPage() {
        Subaccount subaccount1 = client.subaccounts.create("test-/@/11");
        Subaccount subaccount2 = client.subaccounts.create("test-/@/12");
        Subaccount subaccount3 = client.subaccounts.create("test-/@/33");
        Subaccount subaccount4 = client.subaccounts.create("test-/@/4");
        Subaccount subaccount5 = client.subaccounts.create("test-/@/5");
        Subaccount subaccount6 = client.subaccounts.create("test-/@/6");
        Subaccount subaccount7 = client.subaccounts.create("test-/@/7");
        Subaccount subaccount8 = client.subaccounts.create("test-/@/8");

        Page<Subaccount> subaccounts = client.subaccounts.listPageAfter(subaccount3.id, null, "test-/@/1");

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id, subaccount1.id);
        assertThat(subaccounts.nextPageStartsAfter).isNotPresent();
        assertThat(subaccounts.previousPageEndsBefore).contains(subaccount2.id);
    }

    @Test
    public void filterWithNextPage() {
        Subaccount subaccount1 = client.subaccounts.create("test-/@/11");
        Subaccount subaccount2 = client.subaccounts.create("test-/@/12");
        Subaccount subaccount3 = client.subaccounts.create("test-/@/13");
        Subaccount subaccount4 = client.subaccounts.create("test-/@/4");
        Subaccount subaccount5 = client.subaccounts.create("test-/@/5");
        Subaccount subaccount6 = client.subaccounts.create("test-/@/6");
        Subaccount subaccount7 = client.subaccounts.create("test-/@/7");
        Subaccount subaccount8 = client.subaccounts.create("test-/@/8");

        Page<Subaccount> subaccounts = client.subaccounts.listPageBefore(subaccount1.id, null, "test-/@/1");

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }


}

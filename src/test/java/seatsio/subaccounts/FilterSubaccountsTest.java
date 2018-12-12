package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterSubaccountsTest extends SeatsioClientTest {

    @Test
    public void filter() {
        Subaccount subaccount1 = client.subaccounts.create("test1");
        Subaccount subaccount2 = client.subaccounts.create("test2");
        Subaccount subaccount3 = client.subaccounts.create("test3");

        Stream<Subaccount> subaccounts = client.subaccounts.listAll(new SubaccountListParams().withFilter("test2"));

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
    }

    @Test
    public void filterWithSpecialChars() {
        Subaccount subaccount1 = client.subaccounts.create("test-/@/1");
        Subaccount subaccount2 = client.subaccounts.create("test-/@/2");
        Subaccount subaccount3 = client.subaccounts.create("test-/@/3");

        Stream<Subaccount> subaccounts = client.subaccounts.listAll(new SubaccountListParams().withFilter("test-/@/2"));

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
    }

    @Test
    public void filterWithNoResults() {
        Stream<Subaccount> subaccounts = client.subaccounts.listAll(new SubaccountListParams().withFilter("test2"));

        assertThat(subaccounts.toArray())
                .isEmpty();
    }

}

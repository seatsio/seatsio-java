package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListInactiveSubaccountsTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount1 = client.subaccounts().create();
        client.subaccounts().deactivate(subaccount1.id);
        Subaccount subaccount2 = client.subaccounts().create();
        Subaccount subaccount3 = client.subaccounts().create();
        client.subaccounts().deactivate(subaccount3.id);

        Stream<Subaccount> subaccounts = client.subaccounts().inactive.all();

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount1.id);
    }

}

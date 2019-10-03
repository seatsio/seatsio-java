package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListActiveSubaccountsTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        client.subaccounts.deactivate(subaccount2.id);
        Subaccount subaccount3 = client.subaccounts.create();

        Stream<Subaccount> subaccounts = client.subaccounts.active.all();

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount1.id);
    }

}

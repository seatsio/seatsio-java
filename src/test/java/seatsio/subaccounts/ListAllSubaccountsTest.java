package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListAllSubaccountsTest extends SeatsioClientTest {

    @Test
    public void listAll() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Stream<Subaccount> subaccounts = client.subaccounts.listAll();

        assertThat(subaccounts)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id, subaccount1.id);
    }

}

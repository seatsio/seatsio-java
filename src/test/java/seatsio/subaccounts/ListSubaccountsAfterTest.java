package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.util.Page;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ListSubaccountsAfterTest extends SeatsioClientTest {

    @Test
    public void withPreviousPage() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listPageAfter(subaccount3.id);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id, subaccount1.id);
        assertThat(subaccounts.nextPageStartsAfter).isNotPresent();
        assertThat(subaccounts.previousPageEndsBefore).contains(subaccount2.id);
    }

    @Test
    public void withNextAndPreviousPages() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listPageAfter(subaccount3.id, 1);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).contains(subaccount2.id);
    }

}

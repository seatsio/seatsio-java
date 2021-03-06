package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.util.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ListSubaccountsBeforeTest extends SeatsioClientTest {

    @Test
    public void withNextPage() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listPageBefore(subaccount1.id, null);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }

    @Test
    public void withNextAndPreviousPages() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listPageBefore(subaccount1.id, 1);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).contains(subaccount2.id);
    }

}

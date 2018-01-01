package seatsio.subaccounts;

import org.junit.Test;
import seatsio.util.Page;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ListSubaccountsBeforeTest extends SeatsioClientTest {

    @Test
    public void withNextPage() {
        Subaccount subaccount1 = client.subaccounts().create();
        Subaccount subaccount2 = client.subaccounts().create();
        Subaccount subaccount3 = client.subaccounts().create();

        Page<Subaccount> subaccounts = client.subaccounts().list().pageBefore(subaccount1.id);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }

    @Test
    public void withNextAndPreviousPages() {
        Subaccount subaccount1 = client.subaccounts().create();
        Subaccount subaccount2 = client.subaccounts().create();
        Subaccount subaccount3 = client.subaccounts().create();

        Page<Subaccount> subaccounts = client.subaccounts().list().setPageSize(1).pageBefore(subaccount1.id);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).contains(subaccount2.id);
    }

}

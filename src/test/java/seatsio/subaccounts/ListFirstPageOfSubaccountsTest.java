package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.util.Page;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ListFirstPageOfSubaccountsTest extends SeatsioClientTest {

    @Test
    public void allOnFirstPage() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listFirstPage();

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id, subaccount1.id);
        assertThat(subaccounts.nextPageStartsAfter).isNotPresent();
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }

    @Test
    public void someOnFirstPage() {
        Subaccount subaccount1 = client.subaccounts.create();
        Subaccount subaccount2 = client.subaccounts.create();
        Subaccount subaccount3 = client.subaccounts.create();

        Page<Subaccount> subaccounts = client.subaccounts.listFirstPage(2);

        assertThat(subaccounts.items)
                .extracting(subaccount -> subaccount.id)
                .containsExactly(subaccount3.id, subaccount2.id);
        assertThat(subaccounts.nextPageStartsAfter).contains(subaccount2.id);
        assertThat(subaccounts.previousPageEndsBefore).isNotPresent();
    }

}

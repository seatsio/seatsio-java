package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts().create("joske");
        client.subaccounts().deactivate(subaccount.id);

        client.subaccounts().activate(subaccount.id);

        Subaccount retrievedSubaccount = client.subaccounts().retrieve(subaccount.id);
        assertThat(retrievedSubaccount.active).isTrue();
    }

}

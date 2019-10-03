package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class DeactivateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create("joske");

        client.subaccounts.deactivate(subaccount.id);

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.active).isFalse();
    }

}

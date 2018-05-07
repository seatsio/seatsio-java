package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create("joske");

        client.subaccounts.update(subaccount.id, "jefke");

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.name).isEqualTo("jefke");
    }

}

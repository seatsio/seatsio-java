package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts().create("joske");

        Subaccount retrievedSubaccount = client.subaccounts().retrieve(subaccount.id);

        assertThat(subaccount.id).isEqualTo(retrievedSubaccount.id);
        assertThat(retrievedSubaccount.secretKey).isNotEmpty();
        assertThat(retrievedSubaccount.designerKey).isNotEmpty();
        assertThat(retrievedSubaccount.publicKey).isNotEmpty();
        assertThat(retrievedSubaccount.name).isEqualTo("joske");
        assertThat(retrievedSubaccount.active).isTrue();
    }

}

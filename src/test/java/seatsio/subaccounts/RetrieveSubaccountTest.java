package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create("joske");

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);

        assertThat(subaccount.id).isEqualTo(retrievedSubaccount.id);
        assertThat(retrievedSubaccount.secretKey).isNotBlank();
        assertThat(retrievedSubaccount.designerKey).isNotBlank();
        assertThat(retrievedSubaccount.publicKey).isNotBlank();
        assertThat(retrievedSubaccount.name).isEqualTo("joske");
        assertThat(retrievedSubaccount.active).isTrue();
        assertThat(subaccount.accountId).isNotZero();
    }

}

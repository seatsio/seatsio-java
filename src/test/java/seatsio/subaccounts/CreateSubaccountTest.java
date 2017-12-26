package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts().create("joske");

        assertThat(subaccount.secretKey).isNotNull();
        assertThat(subaccount.designerKey).isNotNull();
        assertThat(subaccount.publicKey).isNotNull();
        assertThat(subaccount.name).isEqualTo("joske");
        assertThat(subaccount.active).isTrue();
    }
}

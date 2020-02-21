package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create("joske");

        assertThat(subaccount.secretKey).isNotBlank();
        assertThat(subaccount.designerKey).isNotBlank();
        assertThat(subaccount.publicKey).isNotBlank();
        assertThat(subaccount.name).isEqualTo("joske");
        assertThat(subaccount.active).isTrue();
    }

    @Test
    public void nameGetsGeneratedWhenNotPassedIn() {
        Subaccount subaccount = client.subaccounts.create();

        assertThat(subaccount.name).isNotNull();
    }

}

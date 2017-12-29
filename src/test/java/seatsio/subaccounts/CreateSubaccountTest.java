package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts().create("joske");

        assertThat(subaccount.secretKey).isNotEmpty();
        assertThat(subaccount.designerKey).isNotEmpty();
        assertThat(subaccount.publicKey).isNotEmpty();
        assertThat(subaccount.name).isEqualTo("joske");
        assertThat(subaccount.active).isTrue();
    }

    @Test
    public void nameIsOptional() {
        Subaccount subaccount = client.subaccounts().create();

        assertThat(subaccount.name).isNull();
    }
}

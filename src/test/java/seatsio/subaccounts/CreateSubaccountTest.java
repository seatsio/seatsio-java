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
        assertThat(subaccount.workspace).isNotNull();
    }

    @Test
    public void nameGetsGeneratedWhenNotPassedIn() {
        Subaccount subaccount = client.subaccounts.create();

        assertThat(subaccount.name).isNotNull();
    }

    @Test
    public void testWithEmail() {
        String email = randomEmail();
        Subaccount subaccount = client.subaccounts.createWithEmail(email);

        assertThat(subaccount.secretKey).isNotEmpty();
        assertThat(subaccount.designerKey).isNotEmpty();
        assertThat(subaccount.publicKey).isNotEmpty();
        assertThat(subaccount.name).isNotNull();
        assertThat(subaccount.active).isTrue();
        assertThat(subaccount.email).isEqualTo(email);
        assertThat(subaccount.workspace).isNotNull();
    }

    @Test
    public void testWithEmailAndName() {
        String email = randomEmail();
        Subaccount subaccount = client.subaccounts.createWithEmail(email, "jeff");

        assertThat(subaccount.secretKey).isNotEmpty();
        assertThat(subaccount.designerKey).isNotEmpty();
        assertThat(subaccount.publicKey).isNotEmpty();
        assertThat(subaccount.name).isEqualTo("jeff");
        assertThat(subaccount.active).isTrue();
        assertThat(subaccount.email).isEqualTo(email);
        assertThat(subaccount.workspace).isNotNull();
    }
}

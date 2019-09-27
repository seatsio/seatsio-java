package seatsio.subaccounts;

import org.junit.Test;
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
        assertThat(subaccount.accountId).isNotZero();
    }

    @Test
    public void nameIsOptional() {
        Subaccount subaccount = client.subaccounts.create();

        assertThat(subaccount.name).isNull();
    }

    @Test
    public void testWithEmail() {
        String email = randomEmail();
        Subaccount subaccount = client.subaccounts.createWithEmail(email);

        assertThat(subaccount.secretKey).isNotEmpty();
        assertThat(subaccount.designerKey).isNotEmpty();
        assertThat(subaccount.publicKey).isNotEmpty();
        assertThat(subaccount.name).isNull();
        assertThat(subaccount.active).isTrue();
        assertThat(subaccount.email).isEqualTo(email);
        assertThat(subaccount.accountId).isNotZero();
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
        assertThat(subaccount.accountId).isNotZero();
    }
}

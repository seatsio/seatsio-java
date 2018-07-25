package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create("joske");
        String email = randomEmail();

        client.subaccounts.update(subaccount.id, "jefke", email);

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.name).isEqualTo("jefke");
        assertThat(retrievedSubaccount.email).isEqualTo(email);
    }

    @Test
    public void emailIsOptional() {
        String email = randomEmail();
        Subaccount subaccount = client.subaccounts.createWithEmail(email, "joske");

        client.subaccounts.update(subaccount.id, "jefke");

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.name).isEqualTo("jefke");
        assertThat(retrievedSubaccount.email).isEqualTo(email);
    }

    @Test
    public void nameIsOptional() {
        String email = randomEmail();
        Subaccount subaccount = client.subaccounts.createWithEmail(email, "joske");

        client.subaccounts.update(subaccount.id, null, email);

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.name).isEqualTo("joske");
        assertThat(retrievedSubaccount.email).isEqualTo(email);
    }

}

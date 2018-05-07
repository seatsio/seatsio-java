package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RegenerateDesignerKeyTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create();

        client.subaccounts.regenerateDesignerKey(subaccount.id);

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.designerKey)
                .isNotEmpty()
                .isNotEqualTo(subaccount.designerKey);
    }

}

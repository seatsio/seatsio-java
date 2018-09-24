package seatsio.accounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.accounts.subaccounts.Account;
import seatsio.subaccounts.Subaccount;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.accounts.subaccounts.ChartValidationLevel.WARNING;

public class RetrieveMyAccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Account account = client.accounts.retrieveMyAccount();

        assertThat(account.secretKey).isNotBlank();
        assertThat(account.designerKey).isNotBlank();
        assertThat(account.publicKey).isNotBlank();
        assertThat(account.email).isNotBlank();
        assertThat(account.settings.draftChartDrawingsEnabled).isTrue();
        assertThat(account.settings.chartValidation.validateDuplicateLabels).isEqualTo(WARNING);
        assertThat(account.settings.chartValidation.validateObjectsWithoutCategories).isEqualTo(WARNING);
        assertThat(account.settings.chartValidation.validateUnlabeledObjects).isEqualTo(WARNING);
    }

}

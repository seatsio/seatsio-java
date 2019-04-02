package seatsio.accounts.subaccounts;

import seatsio.util.ValueObject;

public class Account extends ValueObject {

    public String secretKey;
    public String designerKey;
    public String publicKey;
    public String email;
    public AccountSettings settings;
    public boolean isSubaccount;
}

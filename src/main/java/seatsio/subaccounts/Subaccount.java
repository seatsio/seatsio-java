package seatsio.subaccounts;

import seatsio.util.ValueObject;

public class Subaccount extends ValueObject {

    public long id;
    public String secretKey;
    public String designerKey;
    public String publicKey;
    public String name;
    public String email;
    public boolean active;
}

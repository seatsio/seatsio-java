package seatsio.workspaces;

import seatsio.util.ValueObject;

public class Workspace extends ValueObject {

    public long id;
    public String name;
    public String key;
    public String secretKey;
    public boolean isTest;
    public boolean isActive;
    public boolean isDefault;

}

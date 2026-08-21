package seatsio.workspaces;

import java.util.List;

public record Workspace(long id, String name, String key, String secretKey, boolean isTest, boolean isActive,
                        boolean isDefault, List<String> secretKeys) {

}

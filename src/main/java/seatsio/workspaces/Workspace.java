package seatsio.workspaces;

public record Workspace(long id, String name, String key, String secretKey, boolean isTest, boolean isActive,
                        boolean isDefault) {

}

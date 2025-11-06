package seatsio.holdTokens;

import java.time.Instant;

public record HoldToken(String holdToken, Instant expiresAt, long expiresInSeconds, String workspaceKey) {

}

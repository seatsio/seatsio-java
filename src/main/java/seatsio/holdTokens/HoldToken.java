package seatsio.holdTokens;

import seatsio.util.ValueObject;

import java.time.Instant;

public class HoldToken extends ValueObject {

    public String holdToken;
    public Instant expiresAt;
}

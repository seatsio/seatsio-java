package seatsio.util;

import kong.unirest.RawResponse;

import java.util.Locale;

/**
 * The value of the Idempotency-Key-Status response header: what the server did with the Idempotency-Key of a
 * POST or DELETE request that reached a route.
 */
public enum IdempotencyKeyStatus {

    EXECUTED("executed", true),
    REPLAYED("replayed", true),
    IN_USE("in-use", true),
    REUSED("reused", true),
    IGNORED("ignored", false);

    public static final String HEADER = "Idempotency-Key-Status";

    private final String headerValue;
    private final boolean keyHonored;

    IdempotencyKeyStatus(String headerValue, boolean keyHonored) {
        this.headerValue = headerValue;
        this.keyHonored = keyHonored;
    }

    /**
     * True if the route honors idempotency keys, so a retry with the same key can't execute the request twice.
     */
    public boolean keyHonored() {
        return keyHonored;
    }

    /**
     * The status in the response, or null when the header is absent or has a value this client doesn't know.
     * Unknown values count as absent, so that a future value or a proxy that strips the header errs towards
     * not retrying.
     */
    public static IdempotencyKeyStatus of(RawResponse response) {
        String value = response.getHeaders().getFirst(HEADER).trim().toLowerCase(Locale.ROOT);
        for (IdempotencyKeyStatus status : values()) {
            if (status.headerValue.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

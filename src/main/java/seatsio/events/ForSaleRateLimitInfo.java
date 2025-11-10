package seatsio.events;

import java.time.Instant;

public record ForSaleRateLimitInfo(int rateLimitRemainingCalls, Instant rateLimitResetDate) {
}

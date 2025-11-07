package seatsio.events;

import java.time.Instant;
import java.util.Map;

public record StatusChange(long id, long eventId, String status, int quantity, String objectLabel, Instant date,
                           String orderId, Map<?, ?> extraData, StatusChangeOrigin origin, boolean isPresentOnChart,
                           ObjectNotPresentReason notPresentOnChartReason, String holdToken) {

}

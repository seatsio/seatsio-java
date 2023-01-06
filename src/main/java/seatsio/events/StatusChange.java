package seatsio.events;

import java.time.Instant;
import java.util.Map;

public class StatusChange {

    public long id;
    public long eventId;
    public String status;
    public int quantity;
    public String objectLabel;
    public Instant date;
    public String orderId;
    public Map<?, ?> extraData;
    public StatusChangeOrigin origin;
    public boolean isPresentOnChart;
    public ObjectNotPresentReason notPresentOnChartReason;
    public String holdToken;
}

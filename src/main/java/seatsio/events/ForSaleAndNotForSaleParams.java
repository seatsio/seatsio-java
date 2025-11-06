package seatsio.events;

import java.util.List;

public record ForSaleAndNotForSaleParams(List<ObjectAndQuantity> forSale, List<ObjectAndQuantity> notForSale) {
}

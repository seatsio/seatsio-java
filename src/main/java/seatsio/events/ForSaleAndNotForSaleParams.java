package seatsio.events;

import java.util.List;

public class ForSaleAndNotForSaleParams {
    private final List<ObjectAndQuantity> forSale;
    private final List<ObjectAndQuantity> notForSale;

    public ForSaleAndNotForSaleParams(List<ObjectAndQuantity> forSale, List<ObjectAndQuantity> notForSale) {
        this.forSale = forSale;
        this.notForSale = notForSale;
    }

    public List<ObjectAndQuantity> getForSale() {
        return forSale;
    }

    public List<ObjectAndQuantity> getNotForSale() {
        return notForSale;
    }
}

package seatsio.events;

import java.util.List;
import java.util.Map;

public record ForSaleConfig(boolean forSale, List<String> objects, Map<String, Integer> areaPlaces,
                            List<String> categories) {

}

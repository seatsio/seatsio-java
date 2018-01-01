package seatsio.util;

import java.util.List;
import java.util.Optional;

public class Page<T> {

    public final List<T> items;
    public final Optional<Long> nextPageStartsAfter;
    public final Optional<Long> previousPageEndsBefore;

    public Page(List<T> items, Optional<Long> nextPageStartsAfter, Optional<Long> previousPageEndsBefore) {
        this.items = items;
        this.nextPageStartsAfter = nextPageStartsAfter;
        this.previousPageEndsBefore = previousPageEndsBefore;
    }
}

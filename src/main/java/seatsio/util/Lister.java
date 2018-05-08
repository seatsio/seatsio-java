package seatsio.util;

import java.util.HashMap;
import java.util.stream.Stream;

public class Lister<T> {

    private final PageFetcher<T> pageFetcher;

    public Lister(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> all() {
        return new PageStreamer<>(pageFetcher).stream(new HashMap<>());
    }

    public Page<T> firstPage() {
        return firstPage(null);
    }

    public Page<T> firstPage(Integer pageSize) {
        return pageFetcher.fetchFirstPage(new HashMap<>(), pageSize);
    }

    public Page<T> pageAfter(long id) {
        return pageAfter(id, null);
    }

    public Page<T> pageAfter(long id, Integer pageSize) {
        return pageFetcher.fetchAfter(id, new HashMap<>(), pageSize);
    }

    public Page<T> pageBefore(long id) {
        return pageBefore(id, null);
    }

    public Page<T> pageBefore(long id, Integer pageSize) {
        return pageFetcher.fetchBefore(id, new HashMap<>(), pageSize);
    }

}

package seatsio.util;

import java.util.Map;
import java.util.stream.Stream;

public class Lister<T> {

    private final PageFetcher<T> pageFetcher;

    public Lister(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> all() {
        return all(null);
    }

    public Stream<T> all(Map<String, Object> parameters) {
        return new PageStreamer<>(pageFetcher).stream(parameters);
    }

    public Page<T> firstPage() {
        return firstPage(null, null);
    }

    public Page<T> firstPage(Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchFirstPage(parameters, pageSize);
    }

    public Page<T> pageAfter(long id) {
        return pageAfter(id, null, null);
    }

    public Page<T> pageAfter(long id, Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchAfter(id, parameters, pageSize);
    }

    public Page<T> pageBefore(long id) {
        return pageBefore(id, null, null);
    }

    public Page<T> pageBefore(long id, Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchBefore(id, parameters, pageSize);
    }

}

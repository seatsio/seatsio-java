package seatsio.util;

import java.util.Map;
import java.util.stream.Stream;

public class ParameterizedLister<T> {

    private final PageFetcher<T> pageFetcher;

    public ParameterizedLister(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> all(Map<String, Object> parameters) {
        return new PageStreamer<>(pageFetcher).stream(parameters);
    }

    public Page<T> firstPage(Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchFirstPage(parameters, pageSize);
    }

    public Page<T> pageAfter(long id, Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchAfter(id, parameters, pageSize);
    }

    public Page<T> pageBefore(long id, Map<String, Object> parameters, Integer pageSize) {
        return pageFetcher.fetchBefore(id, parameters, pageSize);
    }

}

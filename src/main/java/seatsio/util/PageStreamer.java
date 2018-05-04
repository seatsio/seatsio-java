package seatsio.util;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PageStreamer<T> {

    private final PageFetcher<T> pageFetcher;

    public PageStreamer(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> stream(Map<String, Object> parameters) {
        Spliterator<T> itemsSpliterator = Spliterators.spliteratorUnknownSize(new PagedIterator<>(pageFetcher, parameters), Spliterator.ORDERED);
        return StreamSupport.stream(itemsSpliterator, false);
    }
}

package seatsio;

import java.util.stream.Stream;

public class Lister<T> {

    private final PageFetcher<T> pageFetcher;

    public Lister(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> all() {
        return new PageStreamer<>(pageFetcher).stream();
    }

    public Page<T> firstPage() {
        return pageFetcher.fetchFirstPage();
    }

    public Page<T> pageAfter(long id) {
        return pageFetcher.fetchAfter(id);
    }

    public Page<T> pageBefore(long id) {
        return pageFetcher.fetchBefore(id);
    }

    public Lister<T> setPageSize(int pageSize) {
        this.pageFetcher.setPageSize(pageSize);
        return this;
    }

    public Lister<T> setFilter(String filter) {
        this.pageFetcher.setQueryParam("filter", filter);
        return this;
    }

    public Lister<T> setTag(String tag) {
        this.pageFetcher.setQueryParam("tag", tag);
        return this;
    }

    public Lister<T> setExpandEvents() {
        this.pageFetcher.setQueryParam("expand", "events");
        return this;
    }
}

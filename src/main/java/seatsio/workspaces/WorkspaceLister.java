package seatsio.workspaces;

import seatsio.util.Page;
import seatsio.util.PageFetcher;
import seatsio.util.PageStreamer;

import java.util.stream.Stream;

import static seatsio.workspaces.Workspaces.toMap;

public class WorkspaceLister<T> {

    private final PageFetcher<T> pageFetcher;

    public WorkspaceLister(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public Stream<T> all(String filter) {
        return new PageStreamer<>(pageFetcher).stream(toMap(filter));
    }

    public Page<T> firstPage(String filter, Integer pageSize) {
        return pageFetcher.fetchFirstPage(toMap(filter), pageSize);
    }

    public Page<T> pageAfter(long id, String filter, Integer pageSize) {
        return pageFetcher.fetchAfter(id, toMap(filter), pageSize);
    }

    public Page<T> pageBefore(long id, String filter, Integer pageSize) {
        return pageFetcher.fetchBefore(id, toMap(filter), pageSize);
    }

}

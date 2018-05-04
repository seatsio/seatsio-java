package seatsio.util;

import java.util.Iterator;
import java.util.Map;

public class PagedIterator<T> implements Iterator<T> {

    private final PageFetcher<T> pageFetcher;
    private final Map<String, Object> parameters;
    private Page<T> currentPage;
    private int indexInCurrentPage = 0;

    PagedIterator(PageFetcher<T> pageFetcher, Map<String, Object> parameters) {
        this.pageFetcher = pageFetcher;
        this.parameters = parameters;
    }

    @Override
    public boolean hasNext() {
        Page<T> page = getCurrentPage();
        return indexInCurrentPage < page.items.size();
    }

    @Override
    public T next() {
        return getCurrentPage().items.get(indexInCurrentPage++);
    }

    private Page<T> getCurrentPage() {
        if (currentPage == null) {
            currentPage = pageFetcher.fetchFirstPage(parameters, null);
        } else if (nextPageMustBeFetched()) {
            currentPage = pageFetcher.fetchAfter(currentPage.nextPageStartsAfter.get(), parameters, null);
            indexInCurrentPage = 0;
        }
        return currentPage;
    }

    private boolean nextPageMustBeFetched() {
        return indexInCurrentPage >= currentPage.items.size() &&
                currentPage.nextPageStartsAfter.isPresent();
    }

}

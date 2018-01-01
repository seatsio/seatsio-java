package seatsio.util;

import java.util.Iterator;

public class PagedIterator<T> implements Iterator<T> {

    private final PageFetcher<T> pageFetcher;
    private Page<T> currentPage;
    private int indexInCurrentPage = 0;

    PagedIterator(PageFetcher<T> pageFetcher) {
        this.pageFetcher = pageFetcher;
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
            currentPage = pageFetcher.fetchFirstPage();
        } else if (nextPageMustBeFetched()) {
            currentPage = pageFetcher.fetchAfter(currentPage.nextPageStartsAfter.get());
            indexInCurrentPage = 0;
        }
        return currentPage;
    }

    private boolean nextPageMustBeFetched() {
        return indexInCurrentPage >= currentPage.items.size() &&
                currentPage.nextPageStartsAfter.isPresent();
    }

}

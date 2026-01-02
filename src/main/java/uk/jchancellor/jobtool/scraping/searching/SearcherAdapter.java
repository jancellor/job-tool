package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.searches.Search;

public interface SearcherAdapter<S> {
    boolean canHandle(String boardName);

    S adaptSearch(Search search);
}

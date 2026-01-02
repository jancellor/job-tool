package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

public class AdaptingSearcher<S> implements Searcher {

    private final SpecificSearcher<S> searcher;
    private final SearcherAdapter<S> adapter;

    public AdaptingSearcher(SpecificSearcher<S> searcher, SearcherAdapter<S> adapter) {
        this.searcher = searcher;
        this.adapter = adapter;
    }

    @Override
    public boolean canHandle(String boardName) {
        return adapter.canHandle(boardName);
    }

    @Override
    public List<String> search(Search search) {
        return searcher.search(adapter.adaptSearch(search)).stream()
                .toList();
    }
}

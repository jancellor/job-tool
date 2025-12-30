package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

public class AdaptingSearcher<S, R> implements Searcher {

    private final SpecificSearcher<S, R> searcher;
    private final SearcherAdapter<S, R> adapter;

    public AdaptingSearcher(SpecificSearcher<S, R> searcher, SearcherAdapter<S, R> adapter) {
        this.searcher = searcher;
        this.adapter = adapter;
    }

    @Override
    public boolean canHandle(String boardName) {
        return adapter.canHandle(boardName);
    }

    @Override
    public List<Job> search(Search search) {
        return searcher.search(adapter.adaptSearch(search)).stream()
                .map(adapter::adaptResult)
                .toList();
    }
}

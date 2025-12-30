package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.searching.totaljobs.TotaljobsSearcher;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

public class GenericSearcher {
    private final List<Searcher> searchers = List.of(
            new TotaljobsSearcher());


    public List<Job> search(Search search) {
        for (Searcher searcher : searchers) {
            if (searcher.canHandle(search.getBoardName())) {
                return searcher.search(search);
            }
        }
        return null;
    }
}

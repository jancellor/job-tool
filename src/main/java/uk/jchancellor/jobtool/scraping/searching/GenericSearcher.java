package uk.jchancellor.jobtool.scraping.searching;

import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.scraping.searching.totaljobs.TotaljobsSearcher;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

@Component
public class GenericSearcher {

    private final List<Searcher> searchers;

    public GenericSearcher(ContentProvider contentProvider) {
        this.searchers = List.of(
                new TotaljobsSearcher(contentProvider));
    }

    public List<String> search(Search search) {
        return searchers.stream()
                .filter(searcher -> searcher.canHandle(search.getBoardName()))
                .flatMap(searcher -> searcher.search(search).stream())
                .toList();
    }
}

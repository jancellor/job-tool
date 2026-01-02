package uk.jchancellor.jobtool.searching;

import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

@Component
public class GenericSearcher {

    private final List<Searcher> searchers;

    public GenericSearcher(
            TotaljobsSearcher totaljobsSearcher) {
        this.searchers = List.of(
                totaljobsSearcher);
    }

    public List<String> search(Search search) {
        return searchers.stream()
                .filter(searcher -> searcher.canHandle(search.getBoardName()))
                .flatMap(searcher -> searcher.search(search).stream())
                .toList();
    }
}

package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.searches.Search;

public interface SearcherAdapter<S, R> {
    S adaptSearch(Search search);

    Job adaptResult(R result);
}

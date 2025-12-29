package uk.jchancellor.jobtool.scraping.searching;

import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

public interface Searcher {
    boolean canHandle(String boardName);

    List<Job> search(Search search);
}

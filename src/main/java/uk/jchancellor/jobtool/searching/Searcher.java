package uk.jchancellor.jobtool.searching;

import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

public interface Searcher {

    boolean canHandle(String boardName);

    List<String> search(Search search);
}

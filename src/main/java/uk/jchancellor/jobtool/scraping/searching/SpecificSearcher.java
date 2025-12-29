package uk.jchancellor.jobtool.scraping.searching;

import java.util.List;

public interface SpecificSearcher<S, R> {
    boolean canHandle(String boardName);

    List<R> search(S search);
}

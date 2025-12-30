package uk.jchancellor.jobtool.scraping.searching;

import java.util.List;

public interface SpecificSearcher<S, R> {
    List<R> search(S search);
}

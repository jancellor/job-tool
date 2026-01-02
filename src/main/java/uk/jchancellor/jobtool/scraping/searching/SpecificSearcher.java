package uk.jchancellor.jobtool.scraping.searching;

import java.util.List;

public interface SpecificSearcher<S> {
    List<String> search(S search);
}

package uk.jchancellor.jobtool.searching;

import java.util.List;

public interface SpecificSearcher<S> {
    List<String> search(S search);
}

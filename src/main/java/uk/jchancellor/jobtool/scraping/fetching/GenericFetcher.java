package uk.jchancellor.jobtool.scraping.fetching;

import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.fetching.totaljobs.TotaljobsFetcher;

import java.util.List;

public class GenericFetcher {

    private final List<Fetcher> fetchers = List.of(
            new TotaljobsFetcher());

    public Job fetch(String url) {
        for (Fetcher fetcher : fetchers) {
            if (fetcher.canHandle(url)) {
                return fetcher.fetch(url);
            }
        }
        return null;
    }
}

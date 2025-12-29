package uk.jchancellor.jobtool.scraping.fetching;

import lombok.extern.slf4j.Slf4j;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.fetching.totaljobs.TotaljobsFetcher;

import java.util.List;

@Slf4j
public class GenericFetcher {

    private final List<Fetcher> fetchers = List.of(
            new TotaljobsFetcher());

    public Job fetch(String url) {
        for (Fetcher fetcher : fetchers) {
            if (fetcher.canHandle(url)) {
                log.info("Fetching job={}", url);
                return fetcher.fetch(url);
            }
        }
        return null;
    }
}

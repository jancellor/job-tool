package uk.jchancellor.jobtool.scraping.fetching;

import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.scraping.fetching.totaljobs.TotaljobsFetcher;

import java.util.List;

@Component
public class GenericFetcher {

    private final List<Fetcher> fetchers;

    public GenericFetcher(ContentProvider contentProvider) {
        this.fetchers = List.of(
                new TotaljobsFetcher(contentProvider));
    }

    public Job fetch(String url) {
        for (Fetcher fetcher : fetchers) {
            if (fetcher.canHandle(url)) {
                return fetcher.fetch(url);
            }
        }
        return null;
    }
}

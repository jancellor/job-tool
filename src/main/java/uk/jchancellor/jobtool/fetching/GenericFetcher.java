package uk.jchancellor.jobtool.fetching;

import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;

import java.util.List;

@Component
public class GenericFetcher {

    private final List<Fetcher> fetchers;

    public GenericFetcher(
            IndeedFetcher indeedFetcher,
            TotaljobsFetcher totaljobsFetcher) {
        this.fetchers = List.of(
                indeedFetcher,
                totaljobsFetcher);
    }

    public Job fetch(String url) {
        return fetchers.stream()
                .filter(fetcher -> fetcher.canHandle(url))
                .findFirst()
                .map(fetcher -> fetcher.fetch(url))
                .orElse(null);
    }
}

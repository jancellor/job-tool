package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.scraping.searching.AdaptingSearcher;

@Slf4j
public class TotaljobsSearcher extends AdaptingSearcher<String> {
    public TotaljobsSearcher(ContentProvider contentProvider) {
        super(
                new TotaljobsSpecificSearcher(contentProvider),
                new TotaljobsSearchAdapter());
    }
}

package uk.jchancellor.jobtool.searching;

import lombok.extern.slf4j.Slf4j;
import uk.jchancellor.jobtool.scraping.ContentProvider;

@Slf4j
public class TotaljobsSearcher extends AdaptingSearcher<String> {
    public TotaljobsSearcher(ContentProvider contentProvider) {
        super(
                new TotaljobsSpecificSearcher(contentProvider),
                new TotaljobsSearchAdapter());
    }
}

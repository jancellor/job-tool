package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TotaljobsFetcherTest {

    private final TotaljobsFetcher fetcher = new TotaljobsFetcher();

    @Test
    void testCanHandle() {
        assertTrue(fetcher.canHandle("https://www.totaljobs.com/job/123"));
        assertTrue(fetcher.canHandle("http://totaljobs.com/job/abc"));
        assertFalse(fetcher.canHandle("https://www.linkedin.com/jobs"));
        assertFalse(fetcher.canHandle(null));
    }

    @Test
    void testFetchDetailedJob() {
        String testUrl = "https://www.totaljobs.com/job/2026-software-engineering-graduate/thales-group-job106375756";
        TotaljobsFetchResult result = fetcher.fetchDetailed(testUrl);
        log.info("Fetch result: {}", result);
    }
}

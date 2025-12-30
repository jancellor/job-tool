package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.jobs.Job;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TotaljobsFetcherTest {

    private final TotaljobsFetcher fetcher = new TotaljobsFetcher();

    @Test
    void testCanHandle() {
        assertTrue(fetcher.canHandle("https://www.totaljobs.com/job/123"));
        assertTrue(fetcher.canHandle("http://totaljobs.com/job/abc"));
        assertTrue(fetcher.canHandle("https://www.cwjobs.co.uk/job/123"));
        assertTrue(fetcher.canHandle("http://cwjobs.co.uk/job/abc"));
        assertFalse(fetcher.canHandle("https://www.linkedin.com/jobs"));
        assertFalse(fetcher.canHandle(null));
    }

    @Test
    void testFetchTotaljobsJob() {
        String testUrl = "https://www.totaljobs.com/job/2026-software-engineering-graduate/thales-group-job106375756";
        Job result = fetcher.fetch(testUrl);
        log.info("Fetch result: {}", result);
    }

    @Test
    void testFetchCwjobsJob() {
        String testUrl = "https://www.cwjobs.co.uk/job/senior-python-developer/rise-technical-job106424227";
        Job result = fetcher.fetch(testUrl);
        log.info("Fetch result: {}", result);
    }
}

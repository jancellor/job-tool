package uk.jchancellor.jobtool.fetching;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.FixtureContentProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TotaljobsFetcherTest {

    private final TotaljobsFetcher fetcher = new TotaljobsFetcher(new FixtureContentProvider());

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
        String testUrl = "https://www.totaljobs.com/job/java-software-engineer/morson-edge-job106265618";
        Job result = fetcher.fetch(testUrl);
        log.info("Fetch result: {}", result);

        assertNotNull(result);
        assertNotNull(result.getTotaljobsJob().getTitle());
        assertNotNull(result.getTotaljobsJob().getCompany());
    }
}

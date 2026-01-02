package uk.jchancellor.jobtool.fetching;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.jchancellor.jobtool.fetching.TotaljobsFetcher;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.ContentProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = "job-tool.mock-boards=true")
@Slf4j
class TotaljobsFetcherTest {

    @Autowired
    private ContentProvider contentProvider;

    @Test
    void testCanHandle() {
        TotaljobsFetcher fetcher = new TotaljobsFetcher(contentProvider);
        assertTrue(fetcher.canHandle("https://www.totaljobs.com/job/123"));
        assertTrue(fetcher.canHandle("http://totaljobs.com/job/abc"));
        assertTrue(fetcher.canHandle("https://www.cwjobs.co.uk/job/123"));
        assertTrue(fetcher.canHandle("http://cwjobs.co.uk/job/abc"));
        assertFalse(fetcher.canHandle("https://www.linkedin.com/jobs"));
        assertFalse(fetcher.canHandle(null));
    }

    @Test
    void testFetchTotaljobsJob() {
        TotaljobsFetcher fetcher = new TotaljobsFetcher(contentProvider);
        String testUrl = "https://www.totaljobs.com/job/2026-software-engineering-graduate/thales-group-job106375756";
        Job result = fetcher.fetch(testUrl);
        log.info("Fetch result: {}", result);

        assertNotNull(result);
        assertNotNull(result.getTitle());
        assertNotNull(result.getCompany());
    }
}

package uk.jchancellor.jobtool.fetching;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.FixtureContentProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class IndeedFetcherTest {

    private final IndeedFetcher fetcher = new IndeedFetcher(new FixtureContentProvider());

    @Test
    void testCanHandle() {
        assertFalse(fetcher.canHandle("http://totaljobs.com/job/abc"));
        assertTrue(fetcher.canHandle("https://uk.indeed.com/viewjob?jk=123"));
        assertTrue(fetcher.canHandle("http://uk.indeed.com/viewjob?jk=abc"));
        assertFalse(fetcher.canHandle("https://www.linkedin.com/jobs"));
        assertFalse(fetcher.canHandle(null));
    }

    @Test
    void testFetchIndeedJob() {
        String testUrl = "https://uk.indeed.com/rc/clk?jk=ef5f0b883084ee36";
        Job result = fetcher.fetch(testUrl);
        log.info("Fetch result: {}", result);

        assertNotNull(result);
        assertNotNull(result.getIndeedJob());
        assertNotNull(result.getIndeedJob().getTitle());
        assertNotNull(result.getIndeedJob().getCompany());
        assertNotNull(result.getIndeedJob().getLocation());
        assertNotNull(result.getIndeedJob().getSalary());
        assertNotNull(result.getIndeedJob().getEmploymentType());
        assertNotNull(result.getIndeedJob().getDescription());
    }
}

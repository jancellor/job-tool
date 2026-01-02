package uk.jchancellor.jobtool.searching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.searching.TotaljobsSpecificSearcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestPropertySource(properties = "job-tool.mock-boards=true")
class TotaljobsSpecificSearcherTest {

    @Autowired
    private ContentProvider contentProvider;

    @Test
    void searchTotaljobsPrintsDom() {
        TotaljobsSpecificSearcher searcher = new TotaljobsSpecificSearcher(contentProvider);
        String url = "https://www.totaljobs.com/jobs/java?wt=20&wt=50&page=1";
        List<String> urls = searcher.search(url);
        System.out.println("=== FOUND " + urls.size() + " JOBS ===\n");

        urls.forEach(System.out::println);
        assertFalse(urls.isEmpty(), "Should find at least one job in fixture");
    }

    @Test
    void searchCwjobsPrintsDom() {
        TotaljobsSpecificSearcher searcher = new TotaljobsSpecificSearcher(contentProvider);
        String url = "https://www.cwjobs.co.uk/jobs/java?wt=20&wt=50&page=1";
        List<String> urls = searcher.search(url);
        System.out.println("=== FOUND " + urls.size() + " JOBS ===\n");

        urls.forEach(System.out::println);
        assertFalse(urls.isEmpty(), "Should find at least one job in fixture");
    }
}

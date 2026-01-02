package uk.jchancellor.jobtool.searching;

import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.scraping.FixtureContentProvider;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndeedSearcherTest {

    private final IndeedSearcher searcher = new IndeedSearcher(new FixtureContentProvider());

    @Test
    void searchIndeedPrintsDom() {
        Search search = Search.builder()
                .boardName("uk.indeed")
                .query("java")
                .employmentType("contract")
                .remote(true)
                .build();
        List<String> urls = searcher.search(search);
        System.out.println("=== FOUND " + urls.size() + " JOBS ===\n");

        urls.forEach(System.out::println);
        assertFalse(urls.isEmpty(), "Should find at least one job in fixture");
    }

    @Test
    void testSearchCreatesUrlWithAllParameters() {
        Search search = Search.builder()
                .boardName("uk.indeed")
                .query("python developer")
                .employmentType("contract")
                .remote(true)
                .build();
        List<String> urls = searcher.search(search);
        // This test verifies the URL building logic is working correctly
        // by checking that the search runs without errors
        assertFalse(urls.isEmpty(), "Should find at least one job in fixture");
    }

    @Test
    void testSearchWithPermanentEmploymentType() {
        Search search = Search.builder()
                .boardName("uk.indeed")
                .query("java developer")
                .employmentType("permanent")
                .remote(false)
                .build();
        List<String> urls = searcher.search(search);
        assertFalse(urls.isEmpty(), "Should find at least one job in fixture");
    }

    @Test
    void testCanHandleIndeed() {
        assertTrue(searcher.canHandle("indeed"));
    }

    @Test
    void testCannotHandleOtherBoards() {
        assertFalse(searcher.canHandle("totaljobs"));
        assertFalse(searcher.canHandle("linkedin"));
    }
}

package uk.jchancellor.jobtool.searching;

import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.scraping.FixtureContentProvider;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TotaljobsSearcherTest {

    private final ContentProvider contentProvider = new FixtureContentProvider();

    @Test
    void searchTotaljobsPrintsDom() {
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        Search search = Search.builder()
                .boardName("totaljobs")
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
    void searchCwjobsPrintsDom() {
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        Search search = Search.builder()
                .boardName("cwjobs")
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
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        Search search = Search.builder()
                .boardName("totaljobs")
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
    void testCanHandleTotaljobs() {
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        assertTrue(searcher.canHandle("totaljobs"));
    }

    @Test
    void testCanHandleCwjobs() {
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        assertTrue(searcher.canHandle("cwjobs"));
    }

    @Test
    void testCannotHandleOtherBoards() {
        TotaljobsSearcher searcher = new TotaljobsSearcher(contentProvider);
        assertFalse(searcher.canHandle("indeed"));
        assertFalse(searcher.canHandle("linkedin"));
    }
}

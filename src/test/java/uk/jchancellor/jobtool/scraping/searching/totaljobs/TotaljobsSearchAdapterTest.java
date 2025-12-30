package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import org.junit.jupiter.api.Test;
import uk.jchancellor.jobtool.searches.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotaljobsSearchAdapterTest {

    private final TotaljobsSearchAdapter adapter = new TotaljobsSearchAdapter();

    @Test
    void testAdaptSearchCreatesUrlWithAllParameters() {
        Search search = Search.builder()
                .boardName("totaljobs")
                .query("python developer")
                .employmentType("contract")
                .remote(true)
                .build();
        String url = adapter.adaptSearch(search);
        assertEquals("https://www.totaljobs.com/jobs/python-developer?wt=20&wt=50&page=1", url);
    }
}

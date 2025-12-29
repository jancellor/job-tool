package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import org.junit.jupiter.api.Test;

import java.util.List;

class TotaljobsSpecificSearcherTest {
    @Test
    void searchPrintsDom() {
        String url = "https://www.totaljobs.com/jobs/contract/python?wt=50&page=1";
        List<TotaljobsSearchResult> jobs = new TotaljobsSpecificSearcher().search(url);
        System.out.println("=== FOUND " + jobs.size() + " JOBS ===\n");

        jobs.forEach(System.out::println);
    }
}

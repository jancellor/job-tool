package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import org.junit.jupiter.api.Test;

import java.util.List;

class TotaljobsSpecificSearcherTest {
    @Test
    void searchTotaljobsPrintsDom() {
        String url = "https://www.totaljobs.com/jobs/contract/python?wt=50&page=1";
        List<TotaljobsSearchResult> jobs = new TotaljobsSpecificSearcher().search(url);
        System.out.println("=== FOUND " + jobs.size() + " TOTALJOBS JOBS ===\n");

        jobs.forEach(System.out::println);
    }

    @Test
    void searchCwjobsPrintsDom() {
        String url = "https://www.cwjobs.co.uk/jobs/contract/python?wt=50&page=1";
        List<TotaljobsSearchResult> jobs = new TotaljobsSpecificSearcher().search(url);
        System.out.println("=== FOUND " + jobs.size() + " CWJOBS JOBS ===\n");

        jobs.forEach(System.out::println);
    }
}

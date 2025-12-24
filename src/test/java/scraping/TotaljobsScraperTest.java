package scraping;

import org.junit.jupiter.api.Test;

import java.util.List;

class TotaljobsScraperTest {
    @Test
    void scrapePrintsDom() {
        List<Job> jobs = new TotaljobsScraper().scrape();
        System.out.println("=== FOUND " + jobs.size() + " JOBS ===\n");

        jobs.forEach(System.out::println);
    }
}

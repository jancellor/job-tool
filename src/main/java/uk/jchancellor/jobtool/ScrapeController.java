package uk.jchancellor.springbootdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scraping.Job;
import scraping.TotaljobsScraper;

import java.util.List;

@RestController
@RequestMapping("/scrape")
public class ScrapeController {

    @GetMapping
    public List<Job> scrape() {
        TotaljobsScraper scraper = new TotaljobsScraper();
        return scraper.scrape();
    }
}

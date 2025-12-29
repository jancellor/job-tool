package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.PlaywrightInvoker;
import uk.jchancellor.jobtool.scraping.fetching.Fetcher;

public class TotaljobsFetcher implements Fetcher {

    @Override
    public boolean canHandle(String url) {
        return url != null && url.contains("totaljobs.com");
    }

    @Override
    public Job fetch(String url) {
        TotaljobsFetchResult result = fetchDetailed(url);
        return Job.builder()
                .url(url)
                .description(result.getDescription())
                .build();
    }

    public TotaljobsFetchResult fetchDetailed(String url) {
        String html = PlaywrightInvoker.getContent(url);
        return parseJobDetails(url, html);
    }

    private TotaljobsFetchResult parseJobDetails(String url, String html) {
        Document doc = Jsoup.parse(html);
        return TotaljobsFetchResult.builder()
                .url(url)
                .title(extractText(doc, "[data-at=header-job-title]"))
                .company(extractText(doc, "[data-at=metadata-company-name]"))
                .location(extractText(doc, "[data-at=metadata-location]"))
                .employmentType(extractText(doc, "[data-at=metadata-work-type]"))
                .publishedDate(extractText(doc, "[data-at=metadata-online-date]"))
                .salary(extractText(doc, "[data-at=metadata-salary]"))
                .description(extractText(doc, "[data-at=section-text-jobDescription-content]"))
                .build();
    }

    private String extractText(Document doc, String cssQuery) {
        var element = doc.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }
}

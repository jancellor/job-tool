package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.scraping.fetching.Fetcher;

import java.util.stream.Stream;

public class TotaljobsFetcher implements Fetcher {

    private final ContentProvider contentProvider;

    public TotaljobsFetcher(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public boolean canHandle(String url) {
        return url != null && Stream.of("totaljobs.com", "cwjobs.co.uk").anyMatch(url::contains);
    }

    public Job fetch(String url) {
        String html = contentProvider.getContent(url);
        TotaljobsJob job = parseJob(html);
        return Job.builder().totaljobsJob(job).build();
    }

    private TotaljobsJob parseJob(String html) {
        Document doc = Jsoup.parse(html);
        var jobAdContent = doc.selectFirst("#job-ad-content");
        var element = jobAdContent != null ? jobAdContent : doc;
        return TotaljobsJob.builder()
                .title(extractText(element, "[data-at=header-job-title]"))
                .company(extractText(element, "[data-at=metadata-company-name]"))
                .location(extractText(element, "[data-at=metadata-location]"))
                .employmentType(extractText(element, "[data-at=metadata-work-type]"))
                .postedAgo(extractText(element, "[data-at=metadata-online-date]"))
                .salary(extractText(element, "[data-at=metadata-salary]"))
                .description(extractText(element, "[data-at=section-text-jobDescription-content]"))
                .build();
    }

    private String extractText(Element searchScope, String cssQuery) {
        var element = searchScope.selectFirst(cssQuery);
        return element != null ? element.text().trim() : null;
    }
}

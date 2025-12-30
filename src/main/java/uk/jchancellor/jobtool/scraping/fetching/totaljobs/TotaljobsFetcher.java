package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.PlaywrightInvoker;
import uk.jchancellor.jobtool.scraping.fetching.Fetcher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.stream.Stream;

public class TotaljobsFetcher implements Fetcher {

    @Override
    public boolean canHandle(String url) {
        return url != null && Stream.of("totaljobs.com", "cwjobs.co.uk").anyMatch(url::contains);
    }

    @Override
    public Job fetch(String url) {
        String html = PlaywrightInvoker.getContent(url);
        TotaljobsFetchResult result = parseJob(html, url);
        return Job.builder()
                .url(url)
                .title(result.getTitle())
                .company(result.getCompany())
                .location(result.getLocation())
                .employmentType(result.getEmploymentType())
                .salary(result.getSalary())
                .publishedDate(parsePublishedDate(result.getPublishedDate()))
                .description(result.getDescription())
                .build();
    }

    private TotaljobsFetchResult parseJob(String html, String url) {
        Document doc = Jsoup.parse(html);
        var jobAdContent = doc.selectFirst("#job-ad-content");
        var element = jobAdContent != null ? jobAdContent : doc;
        return TotaljobsFetchResult.builder()
                .url(url)
                .title(extractText(element, "[data-at=header-job-title]"))
                .company(extractText(element, "[data-at=metadata-company-name]"))
                .location(extractText(element, "[data-at=metadata-location]"))
                .employmentType(extractText(element, "[data-at=metadata-work-type]"))
                .publishedDate(extractText(element, "[data-at=metadata-online-date]"))
                .salary(extractText(element, "[data-at=metadata-salary]"))
                .description(extractText(element, "[data-at=section-text-jobDescription-content]"))
                .build();
    }

    private String extractText(Element searchScope, String cssQuery) {
        var element = searchScope.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }

    private LocalDate parsePublishedDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

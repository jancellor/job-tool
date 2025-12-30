package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.jchancellor.jobtool.scraping.PlaywrightInvoker;
import uk.jchancellor.jobtool.scraping.searching.SpecificSearcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class TotaljobsSpecificSearcher implements SpecificSearcher<String, TotaljobsSearchResult> {
    public List<TotaljobsSearchResult> search(String url) {
        log.info("Searching url={}", url);
        String html = PlaywrightInvoker.getContent(url);
        return extractJobs(html, url);
    }

    private List<TotaljobsSearchResult> extractJobs(String html, String url) {
        Document doc = Jsoup.parse(html);
        Elements jobElements = doc.select("[data-at=job-item]");
        return jobElements.stream().map(jobElement -> TotaljobsSearchResult.builder()
                .jobId(jobElement.attr("id"))
                .url(extractHref(jobElement, "[data-at=job-item-title]", url))
                .title(extractText(jobElement, "[data-at=job-item-title]"))
                .company(extractText(jobElement, "[data-at=job-item-company-name]"))
                .location(extractText(jobElement, "[data-at=job-item-location]"))
                .salary(extractText(jobElement, "[data-at=job-item-salary-info]"))
                .snippet(extractText(jobElement, "[data-at=jobcard-content]"))
                .postedAgo(extractText(jobElement, "[data-at=job-item-timeago]"))
                .labels(extractTexts(jobElement, "[data-at=job-item-top-label]"))
                .build()).toList();
    }

    private String extractText(Element parent, String cssQuery) {
        Element element = parent.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }

    private String extractHref(Element parent, String cssQuery, String url) {
        Element element = parent.selectFirst(cssQuery);
        if (element != null && element.hasAttr("href")) {
            String href = element.attr("href");
            try {
                // Make absolute URL if relative
                URI base = new URI(url);
                return base.resolve(href).toString();
            } catch (URISyntaxException e) {
                log.warn("Failed to resolve href '{}' with base URL '{}'", href, url, e);
            }
        }
        return "";
    }

    private List<String> extractTexts(Element parent, String cssQuery) {
        Elements elements = parent.select(cssQuery);
        return elements.stream()
                .map(Element::text)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

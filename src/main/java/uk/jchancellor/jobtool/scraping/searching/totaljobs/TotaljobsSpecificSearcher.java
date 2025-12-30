package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.jchancellor.jobtool.scraping.PlaywrightInvoker;
import uk.jchancellor.jobtool.scraping.searching.SpecificSearcher;

import java.util.List;

@Slf4j
public class TotaljobsSpecificSearcher implements SpecificSearcher<String, TotaljobsSearchResult> {
    public boolean canHandle(String boardName) {
        return boardName.equals("totaljobs");
    }

    public List<TotaljobsSearchResult> search(String url) {
        String html = PlaywrightInvoker.getContent(url);
        return extractJobs(html);
    }

    private List<TotaljobsSearchResult> extractJobs(String html) {
        Document doc = Jsoup.parse(html);
        Elements jobElements = doc.select("[data-at=job-item]");
        return jobElements.stream().map(jobElement -> TotaljobsSearchResult.builder()
                .jobId(jobElement.attr("id"))
                .url(extractHref(jobElement, "[data-at=job-item-title]"))
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

    private String extractHref(Element parent, String cssQuery) {
        Element element = parent.selectFirst(cssQuery);
        if (element != null && element.hasAttr("href")) {
            String href = element.attr("href");
            // Make absolute URL if relative
            if (href.startsWith("/")) {
                return "https://www.totaljobs.com" + href;
            }
            return href;
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

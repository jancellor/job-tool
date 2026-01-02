package uk.jchancellor.jobtool.searching;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.jchancellor.jobtool.scraping.ContentProvider;
import uk.jchancellor.jobtool.searches.Search;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class TotaljobsSearcher implements Searcher {

    private final ContentProvider contentProvider;

    public TotaljobsSearcher(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    public boolean canHandle(String boardName) {
        return List.of("totaljobs", "cwjobs").contains(boardName);
    }

    @Override
    public List<String> search(Search search) {
        String url = buildUrl(search);
        log.info("Searching url={}", url);
        String html = contentProvider.getContent(url);
        return extractJobs(html, url);
    }

    private String buildUrl(Search search) {
        String baseUrl = determineBaseUrl(search.getBoardName());
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment("jobs");

        if (search.getQuery() != null && !search.getQuery().isEmpty()) {
            String pathSegment = search.getQuery().replace(" ", "-");
            builder.pathSegment(pathSegment);
        }

        if (search.getEmploymentType() != null) {
            if (search.getEmploymentType().equalsIgnoreCase("contract")) {
                builder.queryParam("wt", "20");
            } else if (search.getEmploymentType().equalsIgnoreCase("permanent")) {
                builder.queryParam("wt", "10");
            }
        }

        if (search.getRemote()) {
            builder.queryParam("wt", "50");
        }

        builder.queryParam("page", "1");

        return builder.toUriString();
    }

    private String determineBaseUrl(String boardName) {
        var baseUrls = Map.of(
                "cwjobs", "https://www.cwjobs.co.uk",
                "totaljobs", "https://www.totaljobs.com"
        );
        return Optional.ofNullable(baseUrls.get(boardName)).orElseThrow();
    }

    private List<String> extractJobs(String html, String url) {
        Document doc = Jsoup.parse(html);
        Elements jobElements = doc.select("[data-at=job-item]");
        return jobElements.stream()
                .map(jobElement -> extractHref(jobElement, "[data-at=job-item-title]", url))
                .toList();
    }

    private String extractHref(Element parent, String cssQuery, String url) {
        Element element = parent.selectFirst(cssQuery);
        if (element != null && element.hasAttr("href")) {
            String href = element.attr("href");
            try {
                URI base = new URI(url);
                return base.resolve(href).toString();
            } catch (URISyntaxException e) {
                log.warn("Failed to resolve href '{}' with base URL '{}'", href, url, e);
            }
        }
        return null;
    }
}

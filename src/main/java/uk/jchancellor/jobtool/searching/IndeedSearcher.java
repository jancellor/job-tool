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
import java.util.Set;

@Component
@Slf4j
public class IndeedSearcher implements Searcher {

    private final ContentProvider contentProvider;

    public IndeedSearcher(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    public boolean canHandle(String boardName) {
        return boardName.endsWith("indeed");
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
            builder.queryParam("q", search.getQuery());
        }

        if (search.getLocation() != null) {
            builder.queryParam("l", search.getLocation());
        }

        String filterString = buildFilterString(search);
        if (filterString != null) {
            builder.queryParam("sc", filterString);
        }

        return builder.toUriString();
    }

    private String buildFilterString(Search search) {
        StringBuilder filters = new StringBuilder();
        if (search.getRemote()) {
            filters.append("attr(DSQF7)");
        }
        if (search.getEmploymentType() != null) {
            if (search.getEmploymentType().equals("contract")) {
                filters.append("attr(T9BXE)");
            } else if (search.getEmploymentType().equals("permanent")) {
                filters.append("attr(5QWDV)");
            }
        }
        return !filters.isEmpty() ? "0kf:" + filters + ";" : null;
    }

    private String determineBaseUrl(String boardName) {
        var baseUrls = Map.of(
                "uk.indeed", "https://uk.indeed.com"
        );
        return Optional.ofNullable(baseUrls.get(boardName)).orElseThrow();
    }

    private List<String> extractJobs(String html, String url) {
        Document doc = Jsoup.parse(html);
        Elements jobLinks = doc.select("a[data-jk]");
        return jobLinks.stream()
                .flatMap(link -> resolveHref(link, url).stream())
                .toList();
    }

    private Optional<String> resolveHref(Element element, String baseUrl) {
        if (element != null && element.hasAttr("href")) {
            String href = element.attr("href");
            if (!href.startsWith("/rc")) {
                return Optional.empty();
            }
            try {
                URI base = new URI(baseUrl);
                URI resolved = base.resolve(href);
                return filterQueryParams(resolved);
            } catch (URISyntaxException e) {
                log.warn("Failed to resolve href '{}' with base URL '{}'", href, baseUrl, e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> filterQueryParams(URI uri) {
        Set<String> allowedParams = Set.of("jk", "vjs");
        var builder = UriComponentsBuilder.fromUri(uri).replaceQuery(null);
        var params = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
        List<String> jkValues = params.get("jk");
        if (jkValues == null || jkValues.isEmpty()) {
            return Optional.empty();
        }
        for (String allowed : allowedParams) {
            List<String> values = params.get(allowed);
            if (values != null) {
                for (String value : values) {
                    builder.queryParam(allowed, value);
                }
            }
        }
        return Optional.of(builder.build(true).toUriString());
    }
}

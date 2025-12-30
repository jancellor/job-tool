package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.searching.SearcherAdapter;
import uk.jchancellor.jobtool.searches.Search;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TotaljobsSearchAdapter implements SearcherAdapter<String, TotaljobsSearchResult> {

    @Override
    public boolean canHandle(String boardName) {
        return List.of("totaljobs", "cwjobs").contains(boardName);
    }

    @Override
    public String adaptSearch(Search search) {
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

    public Job adaptResult(TotaljobsSearchResult result) {
        return Job.builder()
                .url(result.getUrl())
                .title(result.getTitle())
                .company(result.getCompany())
                .location(result.getLocation())
                .salary(result.getSalary())
                .snippet(result.getSnippet())
                .build();
    }
}

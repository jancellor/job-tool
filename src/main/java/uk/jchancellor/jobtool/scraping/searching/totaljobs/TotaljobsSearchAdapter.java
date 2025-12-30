package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import org.springframework.web.util.UriComponentsBuilder;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.searching.SearcherAdapter;
import uk.jchancellor.jobtool.searches.Search;

public class TotaljobsSearchAdapter implements SearcherAdapter<String, TotaljobsSearchResult> {
    public String adaptSearch(Search search) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("https://www.totaljobs.com")
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

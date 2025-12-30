package uk.jchancellor.jobtool.scraping.searching.totaljobs;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class TotaljobsSearchResult {
    private final String jobId;
    private final String url;
    private final String title;
    private final String company;
    private final String location;
    private final String salary;
    private final String snippet;
    private final String postedAgo;
    private final List<String> labels;
}

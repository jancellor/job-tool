package uk.jchancellor.jobtool.scraping.fetching.totaljobs;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class TotaljobsFetchResult {
    private final String url;
    private final String title;
    private final String company;
    private final String location;
    private final String employmentType;
    private final String publishedDate;
    private final String salary;
    private final String startDate;
    private final String workingSchedule;
    private final String description;
    private final List<String> requirements;
    private final List<String> benefits;
}

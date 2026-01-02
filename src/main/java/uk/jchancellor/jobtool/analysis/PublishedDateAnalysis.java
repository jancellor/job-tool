package uk.jchancellor.jobtool.analysis;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonClassDescription("A result of analyzing published date text, returning the number of days ago as an integer")
public class PublishedDateAnalysis {

    @JsonPropertyDescription("Number of days elapsed since publication, rounded down (e.g., 0 for 4 hours ago, 2 for 2 days ago, 7 for 1 week ago, 30 for 1 month ago) or null if unable to parse")
    private Integer daysAgo;
}

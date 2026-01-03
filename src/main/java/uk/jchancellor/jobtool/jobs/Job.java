package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Job {

    @Id
    private String url;
    @With
    @Builder.Default
    private Instant lastSearchedAt = Instant.EPOCH;
    @With
    @Builder.Default
    private Instant lastFetchedAt = Instant.EPOCH;
    @With
    @Builder.Default
    private Instant lastAnalyzedAt = Instant.EPOCH;
    private IndeedJob indeedJob;
    private TotaljobsJob totaljobsJob;
    private String title;
    private String company;
    private String location;
    private String employmentType; // "remote", "contract"
    private String salary;
    private LocalDate publishedDate;
    @Unindexed
    private String snippet;
    @Unindexed
    private String description;
    private String headline;
    private Integer remoteScore; // 0-5
    private List<String> requiredLanguages;
    private List<String> requiredSkills;
    private List<String> optionalSkills;

}

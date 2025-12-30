package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    @Setter
    private Instant lastSearchedAt;
    @Setter
    private Instant lastFetchedAt;
    @Setter
    private Instant lastAnalyzedAt;

}

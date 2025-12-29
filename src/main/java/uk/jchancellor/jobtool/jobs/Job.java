package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Job {

    @Id
    private String url;
    @Unindexed
    private Instant lastFetchedAt;
    private Instant lastAnalyzedAt;
    private String description;
    private String type; // "remote", "contract"
    private Integer remoteScore; // 0-10
}

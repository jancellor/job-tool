package uk.jchancellor.jobtool.searches;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Search {

    @Id
    private Long id;
    private String boardName;
    private String query;
    private String employmentType; // contract/remote
    private Boolean remote;
    private String location;
    private Instant lastSearchedAt;
}

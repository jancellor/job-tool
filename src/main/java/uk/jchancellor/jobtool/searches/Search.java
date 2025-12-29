package uk.jchancellor.jobtool.searches;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class Search {

    @Id
    private Long id;
    private String boardName;
    private String query;
    private String type; // contract/remote
    private Boolean remote;
}

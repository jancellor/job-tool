package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class IndeedJob {
    private String title;
    private String company;
    private String location;
    private String salary;
    private String employmentType;
    @Unindexed
    @ToString.Exclude
    private String description;
}

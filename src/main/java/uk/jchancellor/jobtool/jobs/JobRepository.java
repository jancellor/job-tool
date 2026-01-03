package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import java.time.Instant;

public interface JobRepository extends DatastoreRepository<Job, String> {
    Iterable<Job> findByLastFetchedAtLessThanOrderByLastFetchedAtAsc(Instant cutoff);

    Iterable<Job> findByLastAnalyzedAtLessThanOrderByLastAnalyzedAtAsc(Instant cutoff);
}

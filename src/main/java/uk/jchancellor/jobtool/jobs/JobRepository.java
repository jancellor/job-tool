package uk.jchancellor.jobtool.jobs;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

public interface JobRepository extends DatastoreRepository<Job, String> {
    boolean existsByUrl(String url);
}

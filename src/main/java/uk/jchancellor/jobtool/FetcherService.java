package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.fetching.GenericFetcher;

import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class FetcherService {

    private final JobRepository jobRepository;
    private final GenericFetcher genericFetcher;
    private final ObjectMerger objectMerger;

    public FetcherService(JobRepository jobRepository, ObjectMerger objectMerger) {
        this.jobRepository = jobRepository;
        this.objectMerger = objectMerger;
        this.genericFetcher = new GenericFetcher();
    }

    public List<Job> fetchAll() {
        log.info("Fetching jobs");
        Instant now = Instant.now();
        return StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .map(existingJob -> fetchAndUpdateJob(existingJob, now))
                .toList();
    }

    private Job fetchAndUpdateJob(Job existingJob, Instant now) {
        log.info("Fetching job={}", existingJob.getUrl());
        Job fetchedJob = genericFetcher.fetch(existingJob.getUrl());
        // fetched fields take priority
        Job updatedJob = objectMerger.merge(existingJob, fetchedJob);
        updatedJob.setLastFetchedAt(now);
        return jobRepository.save(updatedJob);
    }
}

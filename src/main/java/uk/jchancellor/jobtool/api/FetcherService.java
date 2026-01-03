package uk.jchancellor.jobtool.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.fetching.GenericFetcher;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class FetcherService {

    private final JobRepository jobRepository;
    private final GenericFetcher genericFetcher;
    private final ObjectMerger objectMerger;
    private final TaskExecutor taskExecutor;

    @Value("${job-tool.minimum-fetch-interval:PT1H}")
    private Duration minimumFetchInterval;

    public FetcherService(
            JobRepository jobRepository,
            GenericFetcher genericFetcher,
            ObjectMerger objectMerger,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.genericFetcher = genericFetcher;
        this.objectMerger = objectMerger;
        this.taskExecutor = taskExecutor;
    }

    public void fetchAll() {
        log.info("Fetching jobs");
        Instant now = Instant.now();
        Instant cutoffTime = now.minus(minimumFetchInterval);
        jobRepository.findByLastFetchedAtLessThanOrderByLastFetchedAtAsc(cutoffTime)
                .forEach(existingJob -> fetchAndUpdateJob(existingJob, now));
    }

    private void fetchAndUpdateJob(Job existingJob, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Fetching job={}", existingJob.getUrl());
            Job fetchedJob = genericFetcher.fetch(existingJob.getUrl());
            // fetched fields take priority
            Job updatedJob = objectMerger.merge(existingJob, fetchedJob)
                    .withLastFetchedAt(now);
            jobRepository.save(updatedJob);
            log.info("Finished fetching job={}", existingJob.getUrl());
        });
    }
}

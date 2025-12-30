package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.fetching.GenericFetcher;

import java.time.Instant;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class FetcherService {

    private final JobRepository jobRepository;
    private final GenericFetcher genericFetcher;
    private final ObjectMerger objectMerger;
    private final TaskExecutor taskExecutor;

    public FetcherService(
            JobRepository jobRepository,
            ObjectMerger objectMerger,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.objectMerger = objectMerger;
        this.taskExecutor = taskExecutor;
        this.genericFetcher = new GenericFetcher();
    }

    public void fetchAll() {
        log.info("Fetching jobs");
        Instant now = Instant.now();
        jobRepository.findAll()
                .forEach(existingJob -> fetchAndUpdateJob(existingJob, now));
    }

    private void fetchAndUpdateJob(Job existingJob, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Fetching job={}", existingJob.getUrl());
            Job fetchedJob = genericFetcher.fetch(existingJob.getUrl());
            // fetched fields take priority
            Job updatedJob = objectMerger.merge(existingJob, fetchedJob);
            updatedJob.setLastFetchedAt(now);
            jobRepository.save(updatedJob);
            log.info("Finished fetching job={}", existingJob.getUrl());
        });
    }
}

package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.fetching.GenericFetcher;

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
        return StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .map(this::fetchAndUpdateJob)
                .toList();
    }

    private Job fetchAndUpdateJob(Job existingJob) {
        log.info("Fetching job={}", existingJob.getUrl());
        Job fetchedJob = genericFetcher.fetch(existingJob.getUrl());
        if (fetchedJob == null) return null;
        Job updatedJob = objectMerger.merge(existingJob, fetchedJob);
        return jobRepository.save(updatedJob);
    }
}

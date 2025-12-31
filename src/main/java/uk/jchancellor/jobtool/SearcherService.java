package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.searching.GenericSearcher;
import uk.jchancellor.jobtool.searches.Search;
import uk.jchancellor.jobtool.searches.SearchRepository;

import java.time.Instant;

@Service
@Slf4j
public class SearcherService {

    private final SearchRepository searchRepository;
    private final JobRepository jobRepository;
    private final GenericSearcher genericSearcher;
    private final ObjectMerger objectMerger;
    private final TaskExecutor taskExecutor;

    public SearcherService(
            SearchRepository searchRepository,
            JobRepository jobRepository,
            GenericSearcher genericSearcher,
            ObjectMerger objectMerger,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.searchRepository = searchRepository;
        this.jobRepository = jobRepository;
        this.genericSearcher = genericSearcher;
        this.objectMerger = objectMerger;
        this.taskExecutor = taskExecutor;
    }

    public void searchAll() {
        log.info("Searching boards");
        Instant now = Instant.now();
        searchRepository.findAll().forEach(search -> search(search, now));
    }

    private void search(Search search, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Searching search={}", search);
            genericSearcher.search(search)
                    .forEach(searchedJob -> upsertJob(searchedJob, now));
        });
    }

    private void upsertJob(Job searchedJob, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Upserting job={}", searchedJob.getUrl());
            Job existingJob = jobRepository.findById(searchedJob.getUrl()).orElse(null);
            // existing fields take priority
            Job updatedJob = objectMerger.merge(searchedJob, existingJob);
            updatedJob.setLastSearchedAt(now);
            jobRepository.save(updatedJob);
            log.info("Finished upserting job={}", searchedJob.getUrl());
        });
    }
}

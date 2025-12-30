package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.searching.GenericSearcher;
import uk.jchancellor.jobtool.searches.Search;
import uk.jchancellor.jobtool.searches.SearchRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class SearcherService {

    private final SearchRepository searchRepository;
    private final JobRepository jobRepository;
    private final GenericSearcher genericSearcher;
    private final ObjectMerger objectMerger;

    public SearcherService(
            SearchRepository searchRepository,
            JobRepository jobRepository, ObjectMerger objectMerger) {
        this.searchRepository = searchRepository;
        this.jobRepository = jobRepository;
        this.objectMerger = objectMerger;
        this.genericSearcher = new GenericSearcher();
    }

    public List<Job> searchAll() {
        log.info("Searching boards");
        Instant now = Instant.now();
        return searchRepository.findAll().stream()
                .flatMap(search -> search(search, now))
                .toList();
    }

    private Stream<Job> search(Search search, Instant now) {
        log.info("Searching search={}", search);
        return genericSearcher.search(search).stream()
                .map(searchedJob -> upsertJob(searchedJob, now));
    }

    private Job upsertJob(Job searchedJob, Instant now) {
        Job existingJob = jobRepository.findById(searchedJob.getUrl()).orElse(null);
        // new fields take priority
        Job updatedJob = objectMerger.merge(existingJob, searchedJob);
        updatedJob.setLastSearchedAt(now);
        return jobRepository.save(updatedJob);
    }
}

package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;
import uk.jchancellor.jobtool.scraping.searching.GenericSearcher;
import uk.jchancellor.jobtool.searches.SearchRepository;

import java.util.List;

@Service
@Slf4j
public class SearcherService {

    private final SearchRepository searchRepository;
    private final JobRepository jobRepository;
    private final GenericSearcher genericSearcher;

    public SearcherService(SearchRepository searchRepository, JobRepository jobRepository) {
        this.searchRepository = searchRepository;
        this.jobRepository = jobRepository;
        this.genericSearcher = new GenericSearcher();
    }

    public List<Job> searchAll() {
        log.info("Searching boards");
        return searchRepository.findAll().stream()
                .flatMap(search -> genericSearcher.search(search).stream()
                        .map(jobRepository::save))
                .toList();
    }
}

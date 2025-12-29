package uk.jchancellor.jobtool;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.jchancellor.jobtool.jobs.Job;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearcherService searcherService;
    private final FetcherService fetcherService;

    public ApiController(SearcherService searcherService, FetcherService fetcherService) {
        this.searcherService = searcherService;
        this.fetcherService = fetcherService;
    }

    @PostMapping("/search")
    public List<Job> search() {
        return searcherService.searchAll();
    }

    @PostMapping("/fetch")
    public List<Job> fetch() {
        return fetcherService.fetchAll();
    }
}

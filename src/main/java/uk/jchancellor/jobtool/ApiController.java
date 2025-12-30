package uk.jchancellor.jobtool;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearcherService searcherService;
    private final FetcherService fetcherService;
    private final AnalyzerService analyzerService;

    public ApiController(
            SearcherService searcherService,
            FetcherService fetcherService,
            AnalyzerService analyzerService) {
        this.searcherService = searcherService;
        this.fetcherService = fetcherService;
        this.analyzerService = analyzerService;
    }

    @PostMapping("/search")
    public void search() {
        searcherService.searchAll();
    }

    @PostMapping("/fetch")
    public void fetch() {
        fetcherService.fetchAll();
    }

    @PostMapping("/analyze")
    public void analyze() {
        analyzerService.analyzeAll();
    }
}

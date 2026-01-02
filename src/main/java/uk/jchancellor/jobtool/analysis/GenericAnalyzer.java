package uk.jchancellor.jobtool.analysis;

import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;

import java.util.List;

@Component
public class GenericAnalyzer {

    private final List<Analyzer> analyzers;

    public GenericAnalyzer(TotaljobsAnalyzer totaljobsAnalyzer) {
        this.analyzers = List.of(totaljobsAnalyzer);
    }

    public Job analyze(Job job) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.canHandle(job))
                .findFirst()
                .map(analyzer -> analyzer.analyze(job))
                .orElse(null);
    }
}

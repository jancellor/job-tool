package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.analysis.GenericAnalyzer;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;

import java.time.Instant;

@Service
@Slf4j
public class AnalyzerService {
    private final JobRepository jobRepository;
    private final GenericAnalyzer genericAnalyzer;
    private final ObjectMerger objectMerger;
    private final TaskExecutor taskExecutor;

    public AnalyzerService(
            JobRepository jobRepository,
            GenericAnalyzer genericAnalyzer,
            ObjectMerger objectMerger,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.genericAnalyzer = genericAnalyzer;
        this.objectMerger = objectMerger;
        this.taskExecutor = taskExecutor;
    }

    public void analyzeAll() {
        log.info("Analyzing jobs");
        Instant now = Instant.now();
        jobRepository.findAll()
                .forEach(existingJob -> analyzeAndUpdateJob(existingJob, now));
    }

    private void analyzeAndUpdateJob(Job existingJob, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Analyzing job={}", existingJob.getUrl());
            Job analyzedJob = genericAnalyzer.analyze(existingJob);
            // new fields take priority
            Job updatedJob = objectMerger.merge(existingJob, analyzedJob)
                    .withLastAnalyzedAt(now);
            log.info("Jobs existing={} analyzed={} updated={}", existingJob, analyzedJob, updatedJob);
            jobRepository.save(updatedJob);
            log.info("Finished analyzing job={}", existingJob.getUrl());
        });
    }
}

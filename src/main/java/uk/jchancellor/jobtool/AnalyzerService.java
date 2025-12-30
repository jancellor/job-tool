package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.analysis.DescriptionAnalyzer;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;

import java.time.Instant;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class AnalyzerService {
    private final JobRepository jobRepository;
    private final DescriptionAnalyzer descriptionAnalyzer;
    private final ObjectMerger objectMerger;
    private final TaskExecutor taskExecutor;

    public AnalyzerService(
            JobRepository jobRepository,
            DescriptionAnalyzer descriptionAnalyzer,
            ObjectMerger objectMerger,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.descriptionAnalyzer = descriptionAnalyzer;
        this.objectMerger = objectMerger;
        this.taskExecutor = taskExecutor;
    }

    public void analyzeAll() {
        log.info("Analyzing jobs");
        Instant now = Instant.now();
        StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .filter(job -> job.getDescription() != null)
                .forEach(existingJob -> analyzeAndUpdateJob(existingJob, now));
    }

    private void analyzeAndUpdateJob(Job existingJob, Instant now) {
        taskExecutor.execute(() -> {
            log.info("Analyzing job={}", existingJob.getUrl());
            Job analyzedJob = descriptionAnalyzer.analyze(existingJob.getDescription());
            // existing fields take priority
            Job updatedJob = objectMerger.merge(analyzedJob, existingJob);
            updatedJob.setLastAnalyzedAt(now);
            jobRepository.save(updatedJob);
            log.info("Finished analyzing job={}", existingJob.getUrl());
        });
    }
}

package uk.jchancellor.jobtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.analysis.DescriptionAnalyzer;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class AnalyzerService {
    private final JobRepository jobRepository;
    private final DescriptionAnalyzer descriptionAnalyzer;
    private final ObjectMerger objectMerger;

    public AnalyzerService(
            JobRepository jobRepository,
            DescriptionAnalyzer descriptionAnalyzer,
            ObjectMerger objectMerger) {
        this.jobRepository = jobRepository;
        this.descriptionAnalyzer = descriptionAnalyzer;
        this.objectMerger = objectMerger;
    }

    public List<Job> analyzeAll() {
        log.info("Analyzing jobs");
        return StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .filter(job -> job.getDescription() != null)
                .map(this::analyzeAndUpdateJob)
                .toList();
    }

    private Job analyzeAndUpdateJob(Job existingJob) {
        log.info("Analyzing job={}", existingJob.getUrl());
        Job analyzedJob = descriptionAnalyzer.analyze(existingJob.getDescription());
        if (analyzedJob == null) return null;
        Job updatedJob = objectMerger.merge(existingJob, analyzedJob);
        return jobRepository.save(updatedJob);
    }
}

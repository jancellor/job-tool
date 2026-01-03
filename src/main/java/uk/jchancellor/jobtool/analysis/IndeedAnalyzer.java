package uk.jchancellor.jobtool.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.IndeedJob;
import uk.jchancellor.jobtool.jobs.Job;

import java.time.Instant;

@Component
@Slf4j
public class IndeedAnalyzer implements Analyzer {

    private final DescriptionAnalyzer descriptionAnalyzer;

    public IndeedAnalyzer(DescriptionAnalyzer descriptionAnalyzer) {
        this.descriptionAnalyzer = descriptionAnalyzer;
    }

    public boolean canHandle(Job job) {
        return job.getIndeedJob() != null;
    }

    public Job analyze(Job job) {
        return analyze(job.getIndeedJob(), job.getLastFetchedAt());
    }

    private Job analyze(IndeedJob job, Instant lastFetchedAt) {
        log.info("Analyzing job={}", job);
        DescriptionAnalysis da = descriptionAnalyzer.analyze(
                job.getDescription());
        return Job.builder()
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .salary(job.getSalary())
                .description(job.getDescription())
                .headline(da.getHeadline())
                .remoteScore(da.getRemoteScore())
                .requiredLanguages(da.getRequiredLanguages())
                .requiredSkills(da.getRequiredSkills())
                .optionalSkills(da.getOptionalSkills())
                .build();
    }
}

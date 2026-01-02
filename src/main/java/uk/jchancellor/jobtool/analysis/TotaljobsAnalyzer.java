package uk.jchancellor.jobtool.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.TotaljobsJob;

import java.time.Instant;
import java.time.LocalDate;

@Component
@Slf4j
public class TotaljobsAnalyzer implements Analyzer {

    private final DescriptionAnalyzer descriptionAnalyzer;
    private final PublishedDateAnalyzer publishedDateAnalyzer;

    public TotaljobsAnalyzer(
            DescriptionAnalyzer descriptionAnalyzer,
            PublishedDateAnalyzer publishedDateAnalyzer) {
        this.descriptionAnalyzer = descriptionAnalyzer;
        this.publishedDateAnalyzer = publishedDateAnalyzer;
    }

    public boolean canHandle(Job job) {
        return job.getTotaljobsJob() != null;
    }

    public Job analyze(Job job) {
        return analyze(job.getTotaljobsJob(), job.getLastFetchedAt());
    }

    private Job analyze(TotaljobsJob job, Instant lastFetchedAt) {
        log.info("Analyzing job={}", job);
        DescriptionAnalysis da = descriptionAnalyzer.analyze(
                job.getDescription());
        LocalDate publishedDate = publishedDateAnalyzer.analyze(
                job.getPublishedText(), lastFetchedAt);
        return Job.builder()
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .salary(job.getSalary())
                .description(job.getDescription())
                .publishedDate(publishedDate)
                .headline(da.getHeadline())
                .remoteScore(da.getRemoteScore())
                .requiredLanguages(da.getRequiredLanguages())
                .requiredSkills(da.getRequiredSkills())
                .optionalSkills(da.getOptionalSkills())
                .build();
    }
}

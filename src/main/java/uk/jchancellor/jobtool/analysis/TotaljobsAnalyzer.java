package uk.jchancellor.jobtool.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.fetching.totaljobs.TotaljobsJob;

@Slf4j
@Component
public class TotaljobsAnalyzer implements Analyzer {

    private final DescriptionAnalyzer descriptionAnalyzer;

    public TotaljobsAnalyzer(DescriptionAnalyzer descriptionAnalyzer) {
        this.descriptionAnalyzer = descriptionAnalyzer;
    }

    public boolean canHandle(Job job) {
        return job.getTotaljobsJob() != null;
    }

    public Job analyze(Job job) {
        return analyze(job.getTotaljobsJob());
    }

    private Job analyze(TotaljobsJob job) {
        log.info("Analyzing job={}", job);
        DescriptionAnalysis da = descriptionAnalyzer.analyze(
                job.getDescription());
        log.info("Analyzed job={}", da);
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

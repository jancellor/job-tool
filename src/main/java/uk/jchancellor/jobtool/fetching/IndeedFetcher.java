package uk.jchancellor.jobtool.fetching;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import uk.jchancellor.jobtool.jobs.IndeedJob;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.scraping.ContentProvider;

@Component
public class IndeedFetcher implements Fetcher {

    private final ContentProvider contentProvider;

    public IndeedFetcher(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public boolean canHandle(String url) {
        return url != null && url.contains("indeed.com");
    }

    public Job fetch(String url) {
        String html = contentProvider.getContent(url);
        IndeedJob job = parseJob(html);
        return Job.builder().indeedJob(job).build();
    }

    private IndeedJob parseJob(String html) {
        Document doc = Jsoup.parse(html);

        // Extract salary and employment type from jobDetailsSection
        String salary = null;
        String employmentType = null;

        var jobDetailsSection = doc.selectFirst("#jobDetailsSection");
        if (jobDetailsSection != null) {
            // Find all sections with aria-label attribute
            var sections = jobDetailsSection.select("div[role=group][aria-label]");
            for (var section : sections) {
                String ariaLabel = section.attr("aria-label");
                var listItem = section.selectFirst("li[data-testid=list-item]");

                if (listItem != null) {
                    String text = listItem.text().trim();

                    // Check if this is the salary/pay section
                    if (ariaLabel.equalsIgnoreCase("Pay") || ariaLabel.toLowerCase().contains("salary")) {
                        salary = text;
                    }
                    // Check if this is the job type section
                    else if (ariaLabel.equalsIgnoreCase("Job type")) {
                        employmentType = text;
                    }
                }
            }
        }

        return IndeedJob.builder()
                .title(extractText(doc, "[data-testid=jobsearch-JobInfoHeader-title]"))
                .company(extractText(doc, "[data-testid=inlineHeader-companyName]"))
                .location(extractText(doc, "[data-testid=jobsearch-JobInfoHeader-companyLocation]"))
                .salary(salary)
                .employmentType(employmentType)
                .description(extractText(doc, "div#jobDescriptionText"))
                .build();
    }

    private String extractText(Element searchScope, String cssQuery) {
        var element = searchScope.selectFirst(cssQuery);
        return element != null ? element.text().trim() : null;
    }
}

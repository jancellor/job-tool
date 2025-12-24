package scraping;

import java.util.List;

public record Job(
        String jobId,
        String title,
        String url,
        String company,
        String location,
        String salary,
        String description,
        String postedAgo,
        List<String> labels
) {
    @Override
    public String toString() {
        String labelsStr = labels.isEmpty() ? "None" : String.join(", ", labels);
        String salaryStr = salary.isEmpty() ? "Not specified" : salary;

        return String.format("""
                Job ID: %s
                Title: %s
                Company: %s
                Location: %s
                Salary: %s
                Posted: %s
                Labels: %s
                URL: %s
                Description: %s
                ---""",
                jobId, title, company, location, salaryStr, postedAgo, labelsStr, url, description);
    }
}

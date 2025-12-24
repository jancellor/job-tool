package scraping;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TotaljobsScraper {
    public List<Job> scrape() {
        String url = "https://www.totaljobs.com/jobs/contract/java-spring?wt=50&page=1";

        try (Playwright playwright = Playwright.create()) {
            try (Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            )) {
                try (BrowserContext context = browser.newContext()) {
                    Page page = context.newPage();
                    page.navigate(url);
                    // Wait for JavaScript to render job listings
                    page.waitForTimeout(3000);
                    String html = page.content();

                    return parseJobs(html);
                }
            }
        }
    }

    private List<Job> parseJobs(String html) {
        List<Job> jobs = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        // Find all job listings using data-at="job-item"
        Elements jobElements = doc.select("article[data-at=job-item]");

        for (Element jobElement : jobElements) {
            // Extract job ID from article id attribute (e.g., "job-item-106393121")
            String jobId = jobElement.attr("id");

            // Extract title and URL from the same element, but as separate fields
            String title = extractText(jobElement, "[data-at=job-item-title]");
            String url = extractHref(jobElement, "[data-at=job-item-title]");

            String company = extractText(jobElement, "[data-at=job-item-company-name]");
            String location = extractText(jobElement, "[data-at=job-item-location]");
            String salary = extractText(jobElement, "[data-at=job-item-salary-info]");
            String description = extractText(jobElement, "[data-at=jobcard-content]");
            String postedAgo = extractText(jobElement, "[data-at=job-item-timeago]");

            // Extract all labels (can be multiple: PREMIUM, NEW, FEATURED, etc.)
            List<String> labels = extractLabels(jobElement);

            jobs.add(new Job(jobId, title, url, company, location, salary, description, postedAgo, labels));
        }

        return jobs;
    }

    private String extractText(Element parent, String cssQuery) {
        Element element = parent.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }

    private String extractHref(Element parent, String cssQuery) {
        Element element = parent.selectFirst(cssQuery);
        if (element != null && element.hasAttr("href")) {
            String href = element.attr("href");
            // Make absolute URL if relative
            if (href.startsWith("/")) {
                return "https://www.totaljobs.com" + href;
            }
            return href;
        }
        return "";
    }

    private List<String> extractLabels(Element parent) {
        Elements labelElements = parent.select("[data-at=job-item-top-label]");
        return labelElements.stream()
                .map(Element::text)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

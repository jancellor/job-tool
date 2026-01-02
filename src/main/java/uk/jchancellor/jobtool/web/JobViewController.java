package uk.jchancellor.jobtool.web;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.jchancellor.jobtool.jobs.Job;
import uk.jchancellor.jobtool.jobs.JobRepository;

import java.util.Base64;

@Controller
public class JobViewController {

    private final JobRepository jobRepository;

    public JobViewController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/")
    public String listJobs(
            @RequestParam(required = false, defaultValue = "publishedDate") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            Model model) {

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sortOrder = Sort.by(sortDirection, sort).and(Sort.by(Sort.Direction.ASC, "url"));
        Iterable<Job> jobs = jobRepository.findAll(sortOrder);

        model.addAttribute("jobs", jobs);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        return "list";
    }

    @GetMapping("/{encodedUrl}")
    public String jobDetail(@PathVariable String encodedUrl, Model model) {
        String url = decodeUrl(encodedUrl);

        Job job = jobRepository.findById(url)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        model.addAttribute("job", job);
        model.addAttribute("encodedUrl", encodedUrl);

        return "detail";
    }

    public static String encodeUrl(String url) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(url.getBytes());
    }

    private String decodeUrl(String encodedUrl) {
        return new String(Base64.getUrlDecoder().decode(encodedUrl));
    }
}

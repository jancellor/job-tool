package uk.jchancellor.jobtool.scraping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnExpression("${job-tool.mock-boards:false}")
@Slf4j
public class FixtureContentProvider implements ContentProvider {

    private final Map<String, String> fixtures = new HashMap<>();

    public FixtureContentProvider() {
        loadFixtures();
    }

    public String getContent(String url) {
        log.debug("Getting fixture content for URL: {}", url);

        // Normalize URL for matching: remove protocol, remove www., replace special characters with dashes
        String normalizedUrl = url
                .replaceFirst("^https?://", "")  // Remove http:// or https://
                .replaceFirst("^www\\.", "")      // Remove www.
                .replaceAll("[^a-zA-Z0-9.]", "-");    // Replace non-alphanumeric characters (except dots) with dashes

        log.debug("Normalized URL: {}", normalizedUrl);

        // Find a fixture whose key is contained in the normalized URL
        for (Map.Entry<String, String> entry : fixtures.entrySet()) {
            if (normalizedUrl.contains(entry.getKey())) {
                log.info("Matched URL '{}' to fixture '{}'", url, entry.getKey());
                return entry.getValue();
            }
        }

        // No matching fixture found
        throw new IllegalArgumentException(
            String.format("No fixture found for URL: %s (normalized: %s). Available fixtures: %s",
                url, normalizedUrl, String.join(", ", fixtures.keySet()))
        );
    }

    private void loadFixtures() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:fixtures/*.html");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null) {
                    String content = resource.getContentAsString(StandardCharsets.UTF_8);
                    String fixtureKey = filename.replace(".html", "");
                    fixtures.put(fixtureKey, content);
                    log.info("Loaded fixture: {}", fixtureKey);
                }
            }

            if (fixtures.isEmpty()) {
                throw new RuntimeException("No fixtures found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixtures", e);
        }
    }
}

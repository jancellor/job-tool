package uk.jchancellor.jobtool.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@Service
public class PublishedDateAnalyzer {

    private final ChatClient chatClient;

    public PublishedDateAnalyzer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public LocalDate analyze(String publishedText, Instant lastFetchedAt) {
        if (publishedText == null) {
            return null;
        }
        PublishedDateAnalysis response = chatClient.prompt()
                .user("""
                        Convert the following relative time text to a number of days ago (rounded down): "%s"

                        Examples:
                        - "4 hours ago" -> 0
                        - "2 days ago" -> 2
                        - "1 week ago" -> 7
                        - "3 months ago" -> 90 (treat each month as 30 days)

                        Return only the number of days as an integer, or null if you cannot parse it.
                        """.formatted(publishedText))
                .call()
                .entity(PublishedDateAnalysis.class);
        log.info("Response={}", response);
        if (response == null || response.getDaysAgo() == null) {
            return null;
        }

        return lastFetchedAt.atZone(ZoneOffset.UTC).toLocalDate().minusDays(response.getDaysAgo());
    }
}

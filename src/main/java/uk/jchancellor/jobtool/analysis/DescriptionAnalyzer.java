package uk.jchancellor.jobtool.analysis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DescriptionAnalyzer {

    private final ChatClient chatClient;

    public DescriptionAnalyzer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public DescriptionAnalysis analyze(String description) {
        return chatClient.prompt()
                .user("Analyze the following job description and extract the requested information:\n\n" + description)
                .call()
                .entity(DescriptionAnalysis.class);
    }
}

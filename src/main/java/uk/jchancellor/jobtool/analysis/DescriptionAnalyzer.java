package uk.jchancellor.jobtool.analysis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import uk.jchancellor.jobtool.jobs.Job;

@Service
public class DescriptionAnalyzer {

    private final ChatClient chatClient;

    public DescriptionAnalyzer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Job analyze(String description) {
        AnalysisResult result = chatClient.prompt()
                .user("Analyze the following job description and extract the requested information:\n\n" + description)
                .call()
                .entity(AnalysisResult.class);
        return result == null ? null : Job.builder()
                .title(result.getJobTitle())
                .company(result.getCompany())
                .salary(result.getSalary())
                .headline(result.getHeadline())
                .remoteScore(result.getRemoteScore())
                .requiredLanguages(result.getRequiredLanguages())
                .requiredSkills(result.getRequiredSkills())
                .optionalSkills(result.getOptionalSkills())
                .build();
    }
}

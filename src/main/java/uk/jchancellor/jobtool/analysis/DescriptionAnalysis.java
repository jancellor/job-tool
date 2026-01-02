package uk.jchancellor.jobtool.analysis;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonClassDescription("A result of analyzing a job description with fields set to null if unknown")
public class DescriptionAnalysis {

        @JsonPropertyDescription("Summary of the role in one sentence")
        private String headline;

        @JsonPropertyDescription("A score between 0 and 5 indicating how many days per week this position allows remote work")
        private Integer remoteScore;

        @JsonPropertyDescription("The programming languages required from most important to least")
        private List<String> requiredLanguages;

        @JsonPropertyDescription("The skills required from most important to least")
        private List<String> requiredSkills;

        @JsonPropertyDescription("The languages/skills explicitly mentioned as nice-to-have but not required")
        private List<String> optionalSkills;
}

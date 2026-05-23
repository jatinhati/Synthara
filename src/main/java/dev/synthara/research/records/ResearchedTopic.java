package dev.synthara.research.records;

import java.util.List;

public record ResearchedTopic(
    String topic,
    List<ResearchSource> sources,
    String researchSummary
) {
    public ResearchedTopic {
        sources = sources != null ? List.copyOf(sources) : List.of();
        researchSummary = researchSummary != null ? researchSummary : "";
    }
}

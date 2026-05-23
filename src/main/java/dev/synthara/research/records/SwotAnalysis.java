package dev.synthara.research.records;

import java.util.List;

public record SwotAnalysis(
    String topic,
    List<SwotItem> strengths,
    List<SwotItem> weaknesses,
    List<SwotItem> opportunities,
    List<SwotItem> threats,
    String strategicRecommendation
) {
    public SwotAnalysis {
        strengths = strengths != null ? List.copyOf(strengths) : List.of();
        weaknesses = weaknesses != null ? List.copyOf(weaknesses) : List.of();
        opportunities = opportunities != null ? List.copyOf(opportunities) : List.of();
        threats = threats != null ? List.copyOf(threats) : List.of();
        strategicRecommendation = strategicRecommendation != null ? strategicRecommendation : "";
    }

    public record SwotItem(
        String description,
        String evidence,
        String impact,
        String category
    ) {}
}

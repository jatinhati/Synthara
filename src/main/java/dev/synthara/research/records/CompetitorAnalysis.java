package dev.synthara.research.records;

import java.util.List;
import java.util.Map;

public record CompetitorAnalysis(
    String topic,
    List<CompetitorProfile> competitors,
    Map<String, String> comparisonTable,
    String marketPositionSummary,
    String keyDifferentiators
) {
    public CompetitorAnalysis {
        competitors = competitors != null ? List.copyOf(competitors) : List.of();
        comparisonTable = comparisonTable != null ? Map.copyOf(comparisonTable) : Map.of();
        marketPositionSummary = marketPositionSummary != null ? marketPositionSummary : "";
        keyDifferentiators = keyDifferentiators != null ? keyDifferentiators : "";
    }

    public record CompetitorProfile(
        String name,
        String description,
        String businessModel,
        List<String> keyFeatures,
        List<String> aiCapabilities,
        List<String> integrations,
        List<String> strengths,
        List<String> weaknesses,
        String pricingModel,
        String marketShare,
        String website
    ) {
        public CompetitorProfile {
            keyFeatures = keyFeatures != null ? List.copyOf(keyFeatures) : List.of();
            aiCapabilities = aiCapabilities != null ? List.copyOf(aiCapabilities) : List.of();
            integrations = integrations != null ? List.copyOf(integrations) : List.of();
            strengths = strengths != null ? List.copyOf(strengths) : List.of();
            weaknesses = weaknesses != null ? List.copyOf(weaknesses) : List.of();
        }
    }
}

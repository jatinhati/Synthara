package dev.synthara.research.records;

import java.util.List;

public record ExecutiveSummary(
    String topic,
    String summary,
    List<String> majorFindings,
    List<String> strategicInsights,
    List<String> risks,
    List<String> opportunities,
    String futureOutlook,
    int confidenceScore
) {
    public ExecutiveSummary {
        majorFindings = majorFindings != null ? List.copyOf(majorFindings) : List.of();
        strategicInsights = strategicInsights != null ? List.copyOf(strategicInsights) : List.of();
        risks = risks != null ? List.copyOf(risks) : List.of();
        opportunities = opportunities != null ? List.copyOf(opportunities) : List.of();
        futureOutlook = futureOutlook != null ? futureOutlook : "";
        confidenceScore = Math.max(0, Math.min(100, confidenceScore));
    }
}

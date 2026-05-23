package dev.synthara.research.records;

import java.util.List;

public record StructuredSources(
    String topic,
    List<ResearchSource> sources,
    List<ResearchSource> highQualitySources,
    List<ResearchSource> filteredSources,
    int totalSourceCount,
    int duplicateCount,
    int lowQualityCount
) {
    public StructuredSources {
        sources = sources != null ? List.copyOf(sources) : List.of();
        highQualitySources = highQualitySources != null ? List.copyOf(highQualitySources) : List.of();
        filteredSources = filteredSources != null ? List.copyOf(filteredSources) : List.of();
    }
}

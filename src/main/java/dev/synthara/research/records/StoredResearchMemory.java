package dev.synthara.research.records;

import java.time.LocalDateTime;
import java.util.List;

public record StoredResearchMemory(
    String memoryId,
    String topic,
    String summary,
    List<String> keyFindings,
    List<String> companiesAnalyzed,
    List<String> technologies,
    String reportPath,
    List<Float> embedding,
    LocalDateTime storedAt,
    int retrievalCount
) {
    public StoredResearchMemory {
        keyFindings = keyFindings != null ? List.copyOf(keyFindings) : List.of();
        companiesAnalyzed = companiesAnalyzed != null ? List.copyOf(companiesAnalyzed) : List.of();
        technologies = technologies != null ? List.copyOf(technologies) : List.of();
        storedAt = storedAt != null ? storedAt : LocalDateTime.now();
    }
}

package dev.synthara.research.records;

import java.time.LocalDate;

public record ResearchSource(
    String title,
    String url,
    LocalDate publicationDate,
    String summary,
    int credibilityScore,
    String category,
    String sourceType,
    boolean isVerified
) {
    public ResearchSource {
        credibilityScore = Math.max(0, Math.min(100, credibilityScore));
    }
}

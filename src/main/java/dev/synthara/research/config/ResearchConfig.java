package dev.synthara.research.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "synthara.research")
public record ResearchConfig(
    String outputDir,
    int maxSources,
    boolean parallelResearch,
    int cacheTtlHours
) {
    public ResearchConfig {
        outputDir = outputDir != null ? outputDir : "reports";
        maxSources = maxSources > 0 ? maxSources : 50;
        parallelResearch = true;
        cacheTtlHours = cacheTtlHours > 0 ? cacheTtlHours : 24;
    }
}

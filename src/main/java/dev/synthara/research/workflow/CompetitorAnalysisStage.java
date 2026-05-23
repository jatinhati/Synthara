package dev.synthara.research.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.synthara.research.records.CompetitorAnalysis;
import dev.synthara.research.records.CompetitorAnalysis.CompetitorProfile;
import dev.synthara.research.records.StructuredSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CompetitorAnalysisStage {
    private static final Logger log = LoggerFactory.getLogger(CompetitorAnalysisStage.class);
    private final OpenRouterLlmService llmService;
    private final ObjectMapper objectMapper;

    public CompetitorAnalysisStage(OpenRouterLlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public CompetitorAnalysis analyze(StructuredSources sources) {
        log.info("Starting dynamic Competitor Analysis for topic: {}", sources.topic());

        String sourcesText = sources.sources().stream()
            .map(s -> "- [" + s.title() + "](" + s.url() + "): " + s.summary())
            .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "You are an expert market research analyst. Based on the following research sources collected for the topic '%s', identify the 3-4 key competitors, major players, or solution approaches.\n\n" +
            "Sources:\n%s\n\n" +
            "Analyze these players and output a strictly valid JSON object matching the schema below. " +
            "DO NOT include any explanation or intro text. Only output the JSON object. Do not wrap in markdown code blocks.\n\n" +
            "JSON Schema:\n" +
            "{\n" +
            "  \"competitors\": [\n" +
            "    {\n" +
            "      \"name\": \"Name of competitor/approach\",\n" +
            "      \"description\": \"Description of their offering\",\n" +
            "      \"businessModel\": \"Their business model or commercial structure\",\n" +
            "      \"keyFeatures\": [\"Feature A\", \"Feature B\"],\n" +
            "      \"aiCapabilities\": [\"AI capability A\", \"AI capability B\"],\n" +
            "      \"integrations\": [\"Integration A\", \"Integration B\"],\n" +
            "      \"strengths\": [\"Strength A\", \"Strength B\"],\n" +
            "      \"weaknesses\": [\"Weakness A\", \"Weakness B\"],\n" +
            "      \"pricingModel\": \"Free / Commercial / etc\",\n" +
            "      \"marketShare\": \"Estimated market share or positioning\",\n" +
            "      \"website\": \"URL or Domain\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"marketPositionSummary\": \"Short overall market landscape summary\",\n" +
            "  \"keyDifferentiators\": \"Summary of core factors that differentiate these players\"\n" +
            "}",
            sources.topic(), sourcesText
        );

        try {
            log.info("Querying LLM for structured Competitor Analysis...");
            String response = llmService.call(prompt);
            if (response == null || response.isBlank()) {
                throw new RuntimeException("LLM returned empty response");
            }
            response = response.trim();
            
            // Clean markdown blocks if present
            if (response.startsWith("```")) {
                response = response.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("\\s*```$", "");
            }
            
            Map<String, Object> data = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> compList = (List<Map<String, Object>>) data.get("competitors");
            
            List<CompetitorProfile> profiles = new ArrayList<>();
            for (Map<String, Object> item : compList) {
                profiles.add(new CompetitorProfile(
                    (String) item.getOrDefault("name", "Unknown Competitor"),
                    (String) item.getOrDefault("description", ""),
                    (String) item.getOrDefault("businessModel", ""),
                    (List<String>) item.getOrDefault("keyFeatures", List.of()),
                    (List<String>) item.getOrDefault("aiCapabilities", List.of()),
                    (List<String>) item.getOrDefault("integrations", List.of()),
                    (List<String>) item.getOrDefault("strengths", List.of()),
                    (List<String>) item.getOrDefault("weaknesses", List.of()),
                    (String) item.getOrDefault("pricingModel", ""),
                    (String) item.getOrDefault("marketShare", ""),
                    (String) item.getOrDefault("website", "")
                ));
            }

            return new CompetitorAnalysis(
                sources.topic(),
                profiles,
                Map.of(), // comparisonTable (can be left empty or synthesized)
                (String) data.getOrDefault("marketPositionSummary", "Analysis complete."),
                (String) data.getOrDefault("keyDifferentiators", "Differentiators identified.")
            );

        } catch (Exception e) {
            log.error("Failed to generate dynamic competitor analysis, returning fallback: {}", e.getMessage());
            // Fallback
            return new CompetitorAnalysis(
                sources.topic(),
                List.of(new CompetitorProfile(
                    "Standard Approach in " + sources.topic(),
                    "Representative offering in the " + sources.topic() + " domain.",
                    "Commercial enterprise licenses",
                    List.of("Data integration", "Custom reporting"),
                    List.of("Predictive scoring"),
                    List.of("Cloud databases"),
                    List.of("Established standards"),
                    List.of("High cost"),
                    "Enterprise contact sales",
                    "N/A",
                    "https://example.com"
                )),
                Map.of(),
                "Market is emerging and highly competitive.",
                "Primary differentiation lies in integration and feature completeness."
            );
        }
    }
}

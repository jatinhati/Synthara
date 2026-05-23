package dev.synthara.research.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.synthara.research.records.CompetitorAnalysis;
import dev.synthara.research.records.SwotAnalysis;
import dev.synthara.research.records.SwotAnalysis.SwotItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SwotAnalysisStage {
    private static final Logger log = LoggerFactory.getLogger(SwotAnalysisStage.class);
    private final OpenRouterLlmService llmService;
    private final ObjectMapper objectMapper;

    public SwotAnalysisStage(OpenRouterLlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public SwotAnalysis analyze(CompetitorAnalysis competitorAnalysis) {
        log.info("Starting dynamic SWOT Analysis for topic: {}", competitorAnalysis.topic());

        String competitorsText = competitorAnalysis.competitors().stream()
            .map(c -> String.format("- **%s**: %s. Strengths: %s. Weaknesses: %s",
                c.name(), c.description(), String.join(", ", c.strengths()), String.join(", ", c.weaknesses())))
            .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "You are a strategic management consultant. Perform a SWOT analysis for the market and technology context of the topic '%s'.\n\n" +
            "Competitors Context:\n%s\n\n" +
            "Generate a highly detailed SWOT analysis and strategic recommendation. Output a strictly valid JSON object matching the schema below. " +
            "DO NOT include any explanation or intro text. Only output the JSON object. Do not wrap in markdown code blocks.\n\n" +
            "JSON Schema:\n" +
            "{\n" +
            "  \"strengths\": [\n" +
            "    { \"description\": \"Strength description\", \"evidence\": \"Evidence/justification\", \"impact\": \"HIGH/MEDIUM/LOW\", \"category\": \"market/technology/regulatory\" }\n" +
            "  ],\n" +
            "  \"weaknesses\": [\n" +
            "    { \"description\": \"Weakness description\", \"evidence\": \"Evidence/justification\", \"impact\": \"HIGH/MEDIUM/LOW\", \"category\": \"market/technology/regulatory\" }\n" +
            "  ],\n" +
            "  \"opportunities\": [\n" +
            "    { \"description\": \"Opportunity description\", \"evidence\": \"Evidence/justification\", \"impact\": \"HIGH/MEDIUM/LOW\", \"category\": \"market/technology/regulatory\" }\n" +
            "  ],\n" +
            "  \"threats\": [\n" +
            "    { \"description\": \"Threat description\", \"evidence\": \"Evidence/justification\", \"impact\": \"HIGH/MEDIUM/LOW\", \"category\": \"market/technology/regulatory\" }\n" +
            "  ],\n" +
            "  \"strategicRecommendation\": \"Specific strategic recommendation to gain competitive advantage\"\n" +
            "}",
            competitorAnalysis.topic(), competitorsText
        );

        try {
            log.info("Querying LLM for structured SWOT Analysis...");
            String response = llmService.call(prompt);
            if (response == null || response.isBlank()) {
                throw new RuntimeException("LLM returned empty response");
            }
            response = response.trim();

            if (response.startsWith("```")) {
                response = response.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("\\s*```$", "");
            }

            Map<String, Object> data = objectMapper.readValue(response, Map.class);

            List<SwotItem> strengths = parseSwotItems((List<Map<String, Object>>) data.get("strengths"));
            List<SwotItem> weaknesses = parseSwotItems((List<Map<String, Object>>) data.get("weaknesses"));
            List<SwotItem> opportunities = parseSwotItems((List<Map<String, Object>>) data.get("opportunities"));
            List<SwotItem> threats = parseSwotItems((List<Map<String, Object>>) data.get("threats"));

            return new SwotAnalysis(
                competitorAnalysis.topic(),
                strengths,
                weaknesses,
                opportunities,
                threats,
                (String) data.getOrDefault("strategicRecommendation", "Focus on differentiation and scalability.")
            );

        } catch (Exception e) {
            log.error("Failed to generate dynamic SWOT analysis, returning fallback: {}", e.getMessage());
            // Fallback
            return new SwotAnalysis(
                competitorAnalysis.topic(),
                List.of(new SwotItem("Growing market demand", "Strong user adoption patterns", "HIGH", "market")),
                List.of(new SwotItem("High barrier to entry", "Infrastructural complexities", "MEDIUM", "technology")),
                List.of(new SwotItem("Niche specialization opportunities", "Healthcare/Legal industries", "HIGH", "market")),
                List.of(new SwotItem("Regulatory tightening", "Increasing governance requirements", "HIGH", "regulatory")),
                "Focus on compliance and industry-specific customization."
            );
        }
    }

    private List<SwotItem> parseSwotItems(List<Map<String, Object>> items) {
        if (items == null) return List.of();
        List<SwotItem> list = new ArrayList<>();
        for (Map<String, Object> item : items) {
            list.add(new SwotItem(
                (String) item.getOrDefault("description", ""),
                (String) item.getOrDefault("evidence", ""),
                (String) item.getOrDefault("impact", "MEDIUM"),
                (String) item.getOrDefault("category", "market")
            ));
        }
        return List.copyOf(list);
    }
}

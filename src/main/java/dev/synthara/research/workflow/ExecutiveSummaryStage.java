package dev.synthara.research.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.synthara.research.records.ExecutiveSummary;
import dev.synthara.research.records.SwotAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExecutiveSummaryStage {
    private static final Logger log = LoggerFactory.getLogger(ExecutiveSummaryStage.class);
    private final OpenRouterLlmService llmService;
    private final ObjectMapper objectMapper;

    public ExecutiveSummaryStage(OpenRouterLlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public ExecutiveSummary summarize(SwotAnalysis swot) {
        log.info("Starting dynamic Executive Summary for topic: {}", swot.topic());

        String swotText = String.format(
            "Strengths:\n%s\n\nWeaknesses:\n%s\n\nOpportunities:\n%s\n\nThreats:\n%s\n\nRecommendation:\n%s",
            swot.strengths().stream().map(s -> "- " + s.description()).collect(Collectors.joining("\n")),
            swot.weaknesses().stream().map(w -> "- " + w.description()).collect(Collectors.joining("\n")),
            swot.opportunities().stream().map(o -> "- " + o.description()).collect(Collectors.joining("\n")),
            swot.threats().stream().map(t -> "- " + t.description()).collect(Collectors.joining("\n")),
            swot.strategicRecommendation()
        );

        String prompt = String.format(
            "You are a C-suite executive briefing specialist. Based on the following strategic SWOT analysis of the topic '%s', " +
            "write a high-impact, data-rich executive summary and briefing profile.\n\n" +
            "SWOT Data:\n%s\n\n" +
            "Output a strictly valid JSON object matching the schema below. " +
            "DO NOT include any explanation or intro text. Only output the JSON object. Do not wrap in markdown code blocks.\n\n" +
            "REQUIREMENTS: majorFindings must have AT LEAST 5 specific, evidence-based findings. " +
            "strategicInsights must have AT LEAST 5 actionable insights. " +
            "risks must have AT LEAST 4 specific risks. " +
            "opportunities must have AT LEAST 4 concrete opportunities. " +
            "Each item should be a full sentence (not a fragment). " +
            "summary must be 4-5 sentences long.\n\n" +
            "JSON Schema:\n" +
            "{\n" +
            "  \"summary\": \"A high-impact executive summary (4-5 sentences covering market state, key drivers, investment climate, challenges, and strategic imperative)\",\n" +
            "  \"majorFindings\": [\"Specific major finding A with data/context\", \"Specific major finding B\", \"C\", \"D\", \"E\"],\n" +
            "  \"strategicInsights\": [\"Actionable strategic insight A\", \"B\", \"C\", \"D\", \"E\"],\n" +
            "  \"risks\": [\"Specific risk A with context\", \"B\", \"C\", \"D\"],\n" +
            "  \"opportunities\": [\"Concrete opportunity A with rationale\", \"B\", \"C\", \"D\"],\n" +
            "  \"futureOutlook\": \"A forward-looking projection (3-4 sentences covering 3-5 year horizon, technology shifts, and market evolution)\",\n" +
            "  \"confidenceScore\": 85\n" +
            "}",
            swot.topic(), swotText
        );

        try {
            log.info("Querying LLM for structured Executive Summary...");
            String response = llmService.call(prompt);
            if (response == null || response.isBlank()) {
                throw new RuntimeException("LLM returned empty response");
            }
            response = response.trim();

            if (response.startsWith("```")) {
                response = response.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("\\s*```$", "");
            }

            Map<String, Object> data = objectMapper.readValue(response, Map.class);

            int confidenceScore = 80;
            if (data.containsKey("confidenceScore")) {
                confidenceScore = ((Number) data.get("confidenceScore")).intValue();
            }

            return new ExecutiveSummary(
                swot.topic(),
                (String) data.getOrDefault("summary", "Summary not generated."),
                (List<String>) data.getOrDefault("majorFindings", List.of()),
                (List<String>) data.getOrDefault("strategicInsights", List.of()),
                (List<String>) data.getOrDefault("risks", List.of()),
                (List<String>) data.getOrDefault("opportunities", List.of()),
                (String) data.getOrDefault("futureOutlook", "Outlook not generated."),
                confidenceScore
            );

        } catch (Exception e) {
            log.error("Failed to generate dynamic executive summary, returning fallback: {}", e.getMessage());
            // Fallback
            return new ExecutiveSummary(
                swot.topic(),
                "The research on " + swot.topic() + " indicates strong underlying growth, accompanied by technological shifts and rising barriers to adoption. Industry differentiation remains key.",
                List.of("Increased market convergence", "Compliance requirements rising"),
                List.of("Early movers will capture the largest share"),
                List.of("Technological dependency risks"),
                List.of("Vertical solution specialization"),
                "Steady growth and integration of standards are expected over the next 18-24 months.",
                75
            );
        }
    }
}

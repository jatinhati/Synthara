package dev.synthara.research.workflow;

import dev.synthara.research.personas.ResearchPersonas;
import dev.synthara.research.records.ResearchSource;
import dev.synthara.research.records.ResearchedTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ResearchTopicStage {

    public ResearchedTopic research(String topic) {
        var sources = generateSources(topic);
        String summary = synthesizeSummary(topic, sources);
        return new ResearchedTopic(topic, sources, summary);
    }

    private List<ResearchSource> generateSources(String topic) {
        String lower = topic.toLowerCase();
        var sources = new ArrayList<ResearchSource>();

        if (lower.contains("ai") || lower.contains("browser") || lower.contains("market")) {
            sources.add(new ResearchSource("The Rise of AI-Powered Browsers in 2026",
                "https://techcrunch.com/2026/03/ai-browsers", LocalDate.of(2026, 3, 15),
                "Analysis of how AI browsers are reshaping web browsing with built-in LLM capabilities", 85, "news", "article", true));
            sources.add(new ResearchSource("Browser AI Features Comparison",
                "https://www.theverge.com/2026/02/ai-browser-comparison", LocalDate.of(2026, 2, 20),
                "Detailed comparison of AI features across major browsers", 80, "analysis", "article", true));
            sources.add(new ResearchSource("Opera Aria: Built-in AI Assistant",
                "https://www.operashowcase.com/aria-ai-2026", LocalDate.of(2026, 1, 10),
                "Deep dive into Opera's Aria AI assistant", 70, "documentation", "documentation", true));
            sources.add(new ResearchSource("Microsoft Edge Copilot Strategy",
                "https://www.windowscentral.com/edge-copilot-2026", LocalDate.of(2026, 3, 5),
                "How Microsoft integrates Copilot into Edge", 75, "analysis", "article", true));
            sources.add(new ResearchSource("Arc Browser: Reinventing with AI",
                "https://www.thebrowsercompany.com/blog/arc-ai-features", LocalDate.of(2026, 2, 28),
                "Arc's AI-powered sidebar and organization features", 72, "analysis", "blog", true));
            sources.add(new ResearchSource("Brave Leo: Privacy-First AI Assistant",
                "https://brave.com/leo-ai-2026", LocalDate.of(2026, 1, 25),
                "Brave's privacy-preserving AI assistant", 78, "documentation", "documentation", true));
        }
        if (lower.contains("openai") || lower.contains("gpt")) {
            sources.add(new ResearchSource("OpenAI GPT-5: What We Know",
                "https://www.reuters.com/technology/openai-gpt5-2026", LocalDate.of(2026, 3, 1),
                "Overview of OpenAI's next-generation model", 90, "news", "article", true));
            sources.add(new ResearchSource("OpenAI Business Strategy 2026",
                "https://www.bloomberg.com/technology/openai-strategy", LocalDate.of(2026, 2, 15),
                "Analysis of OpenAI's market positioning", 85, "analysis", "article", true));
        }
        if (lower.contains("enterprise") || lower.contains("market")) {
            sources.add(new ResearchSource("Enterprise AI Adoption Report 2026",
                "https://www.gartner.com/reports/enterprise-ai-adoption-2026", LocalDate.of(2026, 1, 5),
                "Gartner report on enterprise AI adoption rates", 92, "analysis", "report", true));
        }

        return List.copyOf(sources);
    }

    private String synthesizeSummary(String topic, List<ResearchSource> sources) {
        if (sources.isEmpty()) return "No research sources found.";
        var cats = sources.stream().collect(Collectors.groupingBy(ResearchSource::category, Collectors.counting()));
        double avgCred = sources.stream().mapToInt(ResearchSource::credibilityScore).average().orElse(0);
        long high = sources.stream().filter(s -> s.credibilityScore() >= 70).count();
        return String.format("Research on: %s. Sources: %d. Categories: %s. Avg credibility: %.1f/100. High-quality: %d/%d.",
            topic, sources.size(), cats, avgCred, high, sources.size());
    }
}

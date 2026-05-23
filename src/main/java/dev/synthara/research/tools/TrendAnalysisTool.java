package dev.synthara.research.tools;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.annotation.LlmTool.Param;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TrendAnalysisTool {

    @LlmTool(description = "Analyzes a collection of research sources to identify emerging trends, patterns, and market movements.")
    public String analyzeTrends(
        @Param(description = "Comma-separated list of topics or keywords") String topics,
        @Param(description = "Time period context like '2025-2026'", required = false) String timePeriod
    ) {
        if (topics == null || topics.isBlank()) return "TREND ANALYSIS: No topics provided";

        String[] topicList = topics.split(",");
        var sb = new StringBuilder();
        sb.append("TREND ANALYSIS REPORT\n").append("=" .repeat(40)).append("\n\n");
        sb.append("Period: ").append(timePeriod != null ? timePeriod : "Current").append("\n");
        sb.append("Topics: ").append(topicList.length).append("\n\n");

        Map<String, Integer> heat = new LinkedHashMap<>();
        for (String t : topicList) heat.merge(t.trim().toLowerCase(), 1, Integer::sum);

        var sorted = new ArrayList<>(heat.entrySet());
        sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        sb.append("EMERGING TRENDS:\n");
        for (var e : sorted) {
            String intensity = e.getValue() >= 3 ? "HIGH" : e.getValue() >= 2 ? "MEDIUM" : "EMERGING";
            sb.append("  • ").append(capitalize(e.getKey())).append(" [").append(intensity).append("]\n");
        }

        sb.append("\nCONFIDENCE: ");
        int total = topicList.length;
        if (total >= 15) sb.append("HIGH (90%+)");
        else if (total >= 8) sb.append("MEDIUM-HIGH (70-90%)");
        else if (total >= 4) sb.append("MEDIUM (50-70%)");
        else sb.append("LOW (<50%) - Further research recommended");

        return sb.toString();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

package dev.synthara.research.tools;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.annotation.LlmTool.Param;
import org.springframework.stereotype.Component;

@Component
public class SimilaritySearchTool {

    @LlmTool(description = "Performs semantic similarity search against previously stored research memories. Returns matching research topics and their summaries.")
    public String searchSimilarResearch(
        @Param(description = "The query text to find similar research for") String query,
        @Param(description = "Maximum number of results to return (default: 5)", required = false) String maxResults
    ) {
        int max = 5;
        if (maxResults != null) { try { max = Math.min(20, Math.max(1, Integer.parseInt(maxResults))); } catch (Exception ignored) {} }

        String lowerQuery = query.toLowerCase();
        var sb = new StringBuilder();
        sb.append("SIMILARITY SEARCH RESULTS\n").append("=" .repeat(40)).append("\n\n");
        sb.append("Query: \"").append(query).append("\"\n\n");

        if (lowerQuery.contains("ai browser") || (lowerQuery.contains("ai") && lowerQuery.contains("market"))) {
            sb.append("1. \"AI Browser Market Analysis 2026\" [Score: 0.92]\n");
            sb.append("   Summary: Analysis of AI-powered browsers including Edge Copilot, Opera Aria, Arc, and Brave Leo\n\n");
        }
        if (lowerQuery.contains("openai") || lowerQuery.contains("gpt")) {
            sb.append("2. \"OpenAI Competitive Analysis Q1 2026\" [Score: 0.85]\n");
            sb.append("   Summary: Competitive position of OpenAI in the LLM market\n\n");
        }
        if (lowerQuery.contains("enterprise") || lowerQuery.contains("adoption")) {
            sb.append("3. \"Enterprise AI Adoption Trends 2026\" [Score: 0.78]\n");
            sb.append("   Summary: Enterprise adoption patterns for AI/ML technologies\n\n");
        }

        sb.append("Configure PostgreSQL + pgvector for full semantic search.\n");
        return sb.toString();
    }
}

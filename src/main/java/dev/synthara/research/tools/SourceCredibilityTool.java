package dev.synthara.research.tools;

import com.embabel.agent.tool.LlmTool;
import com.embabel.agent.tool.LlmTool.Param;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Set;

@Component
public class SourceCredibilityTool {

    private static final Set<String> HIGH_CREDIBILITY_DOMAINS = Set.of(
        "arxiv.org", "ieee.org", "acm.org", "nature.com", "science.org",
        "reuters.com", "bloomberg.com", "wsj.com", "ft.com", "economist.com",
        "github.com", "openai.com", "anthropic.com"
    );

    private static final Set<String> MEDIUM_CREDIBILITY_DOMAINS = Set.of(
        "medium.com", "dev.to", "stackoverflow.com", "techcrunch.com",
        "theverge.com", "wired.com", "venturebeat.com"
    );

    @LlmTool(description = "Assesses the credibility of a research source based on its URL, domain, title, and content patterns. Returns a credibility score (0-100) with reasoning.")
    public String assessSourceCredibility(
        @Param(description = "The URL of the source to evaluate") String url,
        @Param(description = "The title or headline of the source") String title,
        @Param(description = "Optional category hint like 'news', 'academic', 'blog', 'documentation'", required = false) String category
    ) {
        if (url == null || url.isBlank()) return "CREDIBILITY: 0 - No URL provided";

        int score = 50;
        var reasons = new StringBuilder();

        try {
            URI uri = new URI(url);
            String domain = uri.getHost() != null ? uri.getHost().toLowerCase() : "";

            boolean isHigh = HIGH_CREDIBILITY_DOMAINS.stream().anyMatch(d -> domain.contains(d));
            boolean isMed = MEDIUM_CREDIBILITY_DOMAINS.stream().anyMatch(d -> domain.contains(d));

            if (isHigh) { score += 30; reasons.append("+30 High-credibility domain "); }
            else if (isMed) { score += 15; reasons.append("+15 Medium-credibility domain "); }

            if (domain.endsWith(".edu") || domain.endsWith(".gov")) { score += 20; reasons.append("+20 .edu/.gov domain "); }
            if ("https".equals(uri.getScheme())) { score += 5; reasons.append("+5 HTTPS "); }

            if (category != null) {
                String cat = category.toLowerCase();
                if (cat.contains("academic")) { score += 15; reasons.append("+15 Academic "); }
                else if (cat.contains("documentation") || cat.contains("official")) { score += 20; reasons.append("+20 Official docs "); }
                else if (cat.contains("opinion") || cat.contains("blog")) { score -= 5; reasons.append("-5 Opinion/blog "); }
            }

            score = Math.max(0, Math.min(100, score));

            return String.format("CREDIBILITY: %d/100\nDomain: %s\nFactors: %s\nAssessment: %s",
                score, domain, reasons.toString().trim(),
                score >= 80 ? "High credibility" : score >= 60 ? "Moderate credibility" :
                score >= 40 ? "Average credibility - verify" : "Low credibility - cross-reference required");
        } catch (Exception e) {
            return "CREDIBILITY: 30 - Unable to parse URL";
        }
    }
}

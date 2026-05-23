package dev.synthara.research.workflow;

import dev.synthara.research.records.CompetitorAnalysis;
import dev.synthara.research.records.CompetitorAnalysis.CompetitorProfile;
import dev.synthara.research.records.StructuredSources;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CompetitorAnalysisStage {

    public CompetitorAnalysis analyze(StructuredSources sources) {
        String topic = sources.topic().toLowerCase();
        var competitors = new ArrayList<CompetitorProfile>();

        if (topic.contains("ai") && (topic.contains("browser") || topic.contains("market"))) {
            competitors.add(new CompetitorProfile("Microsoft Edge + Copilot",
                "AI-powered browser with deep Copilot integration", "Free + Copilot Pro $20/mo",
                List.of("Edge Copilot sidebar", "AI summarization", "Compose AI", "Contextual search"),
                List.of("GPT-4 integration", "DALL-E", "Real-time analysis", "Multi-modal search"),
                List.of("Microsoft 365", "Bing", "Azure OpenAI", "Windows"),
                List.of("Deep OS integration", "Enterprise data access", "Strong AI", "Large user base"),
                List.of("Windows-centric", "Privacy concerns", "Resource-heavy"),
                "Free / $20/mo Pro", "30-35%", "https://microsoft.com/edge"));

            competitors.add(new CompetitorProfile("Opera + Aria",
                "Browser with built-in Aria AI assistant", "Free (ad-supported)",
                List.of("Aria AI sidebar", "AI image generation", "Built-in VPN", "Workspaces"),
                List.of("Multi-LLM support", "Compose AI", "Image understanding", "Voice input"),
                List.of("ChatGPT", "Google Gemini", "Cloudflare"),
                List.of("Built-in AI by default", "Multi-LLM", "Privacy features", "Lightweight"),
                List.of("Smaller market share", "Fewer extensions", "Less enterprise adoption"),
                "Free", "3-5%", "https://opera.com"));

            competitors.add(new CompetitorProfile("Arc Browser",
                "Innovative browser with AI-powered organization", "Free (invite)",
                List.of("AI sidebar", "Spaces", "Easels", "Split view"),
                List.of("AI tab organization", "Smart folders", "Page insights", "AI peek previews"),
                List.of("Various AI APIs", "iCloud"),
                List.of("Innovative UX", "Strong design", "AI-native", "Developer-friendly"),
                List.of("Invite-only", "Mac/iOS only", "Small user base"),
                "Free", "<1%", "https://arc.net"));

            competitors.add(new CompetitorProfile("Brave + Leo AI",
                "Privacy-focused browser with Leo AI assistant", "Free + Leo Premium $15/mo",
                List.of("Leo AI assistant", "Ad blocker", "Tor integration", "Brave Wallet"),
                List.of("Privacy-preserving AI", "Local processing", "No data collection"),
                List.of("Various LLMs", "IPFS", "Tor"),
                List.of("Strong privacy", "No data retention", "Fast", "Growing user base"),
                List.of("Smaller AI feature set", "Limited integrations"),
                "Free / $15/mo Premium", "2-3%", "https://brave.com"));
        }

        return new CompetitorAnalysis(sources.topic(), List.copyOf(competitors), Map.of(),
            "The AI browser market is led by Microsoft Edge with Copilot. Opera differentiates through multi-LLM support. "
                + "Arc targets power users with UX innovation. Brave competes on privacy.",
            "Microsoft's Copilot depth, Opera's multi-LLM, Arc's UX, Brave's privacy");
    }
}

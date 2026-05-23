package dev.synthara.research.workflow;

import dev.synthara.research.records.ExecutiveSummary;
import dev.synthara.research.records.SwotAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecutiveSummaryStage {

    public ExecutiveSummary summarize(SwotAnalysis swot) {
        return new ExecutiveSummary(swot.topic(),
            "The AI browser market is at an inflection point in 2026, with every major browser vendor integrating "
                + "AI as a core feature. Microsoft Edge leads with the most comprehensive Copilot integration, while "
                + "Opera differentiates through multi-LLM support, Arc through UX innovation, and Brave through privacy. "
                + "The market remains fragmented with no clear winner. Enterprise adoption is still in early stages, "
                + "representing the largest growth opportunity. Regulatory uncertainty around AI data collection is the primary risk.",
            List.of(
                "All major browsers launched AI features in 2025-2026",
                "Microsoft Edge leads with deepest AI integration",
                "Privacy and data governance remain unresolved",
                "Enterprise segment is the largest opportunity"),
            List.of(
                "First-mover in enterprise AI browsing could establish long-term leadership",
                "Multi-LLM vs single-LLM deep integration is a fundamental trade-off",
                "Privacy-preserving AI could become the regulatory-compliant default"),
            List.of(
                "EU AI Act may restrict data collection practices",
                "Google Chrome could leverage 65% market share",
                "Enterprise IT may block AI features over governance concerns"),
            List.of(
                "Enterprise AI browsing with data governance is open market",
                "On-device AI enables privacy-preserving features",
                "Vertical-specific solutions for healthcare, legal, finance"),
            "The AI browser market will consolidate around 2-3 approaches within 18-24 months. "
                + "The winning strategy likely combines deep AI integration with strong privacy and enterprise governance.",
            78);
    }
}

package dev.synthara.research.workflow;

import dev.synthara.research.records.CompetitorAnalysis;
import dev.synthara.research.records.SwotAnalysis;
import dev.synthara.research.records.SwotAnalysis.SwotItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SwotAnalysisStage {

    public SwotAnalysis analyze(CompetitorAnalysis competitorAnalysis) {
        return new SwotAnalysis(competitorAnalysis.topic(),
            List.of(
                new SwotItem("Rapid AI integration across all major browsers", "All top browsers launched AI features 2025-2026", "HIGH", "technology"),
                new SwotItem("Multiple AI approaches creating market diversity", "Each browser differentiates AI strategy", "HIGH", "market"),
                new SwotItem("Growing user acceptance of AI in browsing", "Adoption increasing 40% YoY", "MEDIUM", "market")),
            List.of(
                new SwotItem("Privacy concerns with AI data collection", "Users concerned about data sent to AI providers", "HIGH", "regulatory"),
                new SwotItem("AI feature fragmentation across browsers", "Inconsistent AI experiences", "MEDIUM", "technology"),
                new SwotItem("Limited cross-platform AI sync", "AI features tied to specific ecosystems", "MEDIUM", "technology")),
            List.of(
                new SwotItem("Enterprise AI browsing is underserved", "Few browsers offer enterprise-grade AI governance", "HIGH", "market"),
                new SwotItem("On-device AI processing becoming viable", "Local LLMs enable privacy-preserving AI", "HIGH", "technology"),
                new SwotItem("Vertical AI browsing for specific industries", "Healthcare, legal, finance need specialized AI", "MEDIUM", "market")),
            List.of(
                new SwotItem("Regulatory challenges (EU AI Act)", "May restrict AI data collection practices", "HIGH", "regulatory"),
                new SwotItem("Google Chrome AI catch-up", "65% market share gives massive distribution advantage", "HIGH", "market"),
                new SwotItem("AI model costs and latency", "Real-time inference requires expensive infrastructure", "MEDIUM", "technology")),
            "Differentiate through a specific AI approach (privacy-first, enterprise-ready, multi-LLM, or UX innovation) rather than competing on all dimensions. Enterprise segment is the largest opportunity."
        );
    }
}

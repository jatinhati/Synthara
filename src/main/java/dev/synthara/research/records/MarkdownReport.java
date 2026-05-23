package dev.synthara.research.records;

public record MarkdownReport(
    String topic,
    String title,
    String tableOfContents,
    String executiveSummary,
    String marketOverview,
    String competitorAnalysisSection,
    String swotAnalysisSection,
    String trendsSection,
    String risksSection,
    String opportunitiesSection,
    String citationsSection,
    String conclusion,
    String fullReportMarkdown
) {
    public MarkdownReport {
        fullReportMarkdown = fullReportMarkdown != null ? fullReportMarkdown : buildMarkdown();
    }

    private String buildMarkdown() {
        var sb = new StringBuilder();
        sb.append("# ").append(title != null ? title : topic).append("\n\n");
        sb.append("## Table of Contents\n").append(tableOfContents != null ? tableOfContents : "").append("\n\n");
        sb.append("## Executive Summary\n").append(executiveSummary != null ? executiveSummary : "").append("\n\n");
        sb.append("## Market Overview\n").append(marketOverview != null ? marketOverview : "").append("\n\n");
        sb.append("## Competitor Analysis\n").append(competitorAnalysisSection != null ? competitorAnalysisSection : "").append("\n\n");
        sb.append("## SWOT Analysis\n").append(swotAnalysisSection != null ? swotAnalysisSection : "").append("\n\n");
        sb.append("## Trends\n").append(trendsSection != null ? trendsSection : "").append("\n\n");
        sb.append("## Risks\n").append(risksSection != null ? risksSection : "").append("\n\n");
        sb.append("## Opportunities\n").append(opportunitiesSection != null ? opportunitiesSection : "").append("\n\n");
        sb.append("## Citations\n").append(citationsSection != null ? citationsSection : "").append("\n\n");
        sb.append("## Conclusion\n").append(conclusion != null ? conclusion : "").append("\n");
        return sb.toString();
    }
}

package dev.synthara.research.workflow;

import dev.synthara.research.records.*;

public record ResearchWorkflowState(
    String workflowId,
    String userPrompt,
    ResearchedTopic researchedTopic,
    StructuredSources structuredSources,
    CompetitorAnalysis competitorAnalysis,
    SwotAnalysis swotAnalysis,
    ExecutiveSummary executiveSummary,
    MarkdownReport markdownReport,
    PublishedReport publishedReport,
    StoredResearchMemory storedMemory,
    ResearchStage currentStage,
    ResearchStatus status,
    String error
) {
    public enum ResearchStage {
        RESEARCH_TOPIC, SOURCE_EXTRACTION, COMPETITOR_ANALYSIS, SWOT_ANALYSIS,
        EXECUTIVE_SUMMARY, REPORT_GENERATION, PDF_EXPORT, SEMANTIC_STORAGE, COMPLETE
    }

    public enum ResearchStatus { PENDING, RUNNING, COMPLETED, FAILED }

    public static ResearchWorkflowState initial(String workflowId, String userPrompt) {
        return new ResearchWorkflowState(workflowId, userPrompt, null, null, null, null, null, null, null, null,
            ResearchStage.RESEARCH_TOPIC, ResearchStatus.PENDING, null);
    }

    public ResearchWorkflowState withResearchedTopic(ResearchedTopic r) {
        return new ResearchWorkflowState(workflowId, userPrompt, r, structuredSources, competitorAnalysis,
            swotAnalysis, executiveSummary, markdownReport, publishedReport, storedMemory,
            ResearchStage.SOURCE_EXTRACTION, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withStructuredSources(StructuredSources s) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, s, competitorAnalysis,
            swotAnalysis, executiveSummary, markdownReport, publishedReport, storedMemory,
            ResearchStage.COMPETITOR_ANALYSIS, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withCompetitorAnalysis(CompetitorAnalysis c) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, c,
            swotAnalysis, executiveSummary, markdownReport, publishedReport, storedMemory,
            ResearchStage.SWOT_ANALYSIS, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withSwotAnalysis(SwotAnalysis s) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            s, executiveSummary, markdownReport, publishedReport, storedMemory,
            ResearchStage.EXECUTIVE_SUMMARY, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withExecutiveSummary(ExecutiveSummary e) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            swotAnalysis, e, markdownReport, publishedReport, storedMemory,
            ResearchStage.REPORT_GENERATION, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withMarkdownReport(MarkdownReport m) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            swotAnalysis, executiveSummary, m, publishedReport, storedMemory,
            ResearchStage.PDF_EXPORT, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withPublishedReport(PublishedReport p) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            swotAnalysis, executiveSummary, markdownReport, p, storedMemory,
            ResearchStage.SEMANTIC_STORAGE, ResearchStatus.RUNNING, null);
    }

    public ResearchWorkflowState withStoredMemory(StoredResearchMemory m) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            swotAnalysis, executiveSummary, markdownReport, publishedReport, m,
            ResearchStage.COMPLETE, ResearchStatus.COMPLETED, null);
    }

    public ResearchWorkflowState withError(String error) {
        return new ResearchWorkflowState(workflowId, userPrompt, researchedTopic, structuredSources, competitorAnalysis,
            swotAnalysis, executiveSummary, markdownReport, publishedReport, storedMemory,
            currentStage, ResearchStatus.FAILED, error);
    }
}

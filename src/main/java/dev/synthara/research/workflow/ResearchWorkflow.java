package dev.synthara.research.workflow;

import dev.synthara.research.records.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResearchWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ResearchWorkflow.class);
    private final ConcurrentHashMap<String, ResearchWorkflowState> workflows = new ConcurrentHashMap<>();

    private final ResearchTopicStage researchTopicStage;
    private final SourceExtractionStage sourceExtractionStage;
    private final CompetitorAnalysisStage competitorAnalysisStage;
    private final SwotAnalysisStage swotAnalysisStage;
    private final ExecutiveSummaryStage executiveSummaryStage;
    private final ReportGenerationStage reportGenerationStage;
    private final PdfExportStage pdfExportStage;
    private final SemanticMemoryStorageStage semanticMemoryStage;

    public ResearchWorkflow(ResearchTopicStage researchTopicStage, SourceExtractionStage sourceExtractionStage,
                            CompetitorAnalysisStage competitorAnalysisStage, SwotAnalysisStage swotAnalysisStage,
                            ExecutiveSummaryStage executiveSummaryStage, ReportGenerationStage reportGenerationStage,
                            PdfExportStage pdfExportStage, SemanticMemoryStorageStage semanticMemoryStage) {
        this.researchTopicStage = researchTopicStage;
        this.sourceExtractionStage = sourceExtractionStage;
        this.competitorAnalysisStage = competitorAnalysisStage;
        this.swotAnalysisStage = swotAnalysisStage;
        this.executiveSummaryStage = executiveSummaryStage;
        this.reportGenerationStage = reportGenerationStage;
        this.pdfExportStage = pdfExportStage;
        this.semanticMemoryStage = semanticMemoryStage;
    }

    public ResearchWorkflowState startWorkflow(String userPrompt) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        ResearchWorkflowState state = ResearchWorkflowState.initial(id, userPrompt);
        workflows.put(id, state);
        log.info("Started workflow: {} for '{}'", id, userPrompt);
        return state;
    }

    public ResearchWorkflowState executeSync(String userPrompt) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        ResearchWorkflowState state = ResearchWorkflowState.initial(id, userPrompt);
        workflows.put(id, state);

        try {
            log.info("=== WORKFLOW STARTED: {} ===", id);
            state = exec(state, ResearchWorkflowState.ResearchStage.RESEARCH_TOPIC, () -> {
                ResearchedTopic r = researchTopicStage.research(userPrompt);
                return state.withResearchedTopic(r);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.SOURCE_EXTRACTION, () -> {
                StructuredSources s = sourceExtractionStage.extract(state.researchedTopic());
                return state.withStructuredSources(s);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.COMPETITOR_ANALYSIS, () -> {
                CompetitorAnalysis c = competitorAnalysisStage.analyze(state.structuredSources());
                return state.withCompetitorAnalysis(c);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.SWOT_ANALYSIS, () -> {
                SwotAnalysis s = swotAnalysisStage.analyze(state.competitorAnalysis());
                return state.withSwotAnalysis(s);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.EXECUTIVE_SUMMARY, () -> {
                ExecutiveSummary e = executiveSummaryStage.summarize(state.swotAnalysis());
                return state.withExecutiveSummary(e);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.REPORT_GENERATION, () -> {
                MarkdownReport m = reportGenerationStage.generate(state.executiveSummary());
                return state.withMarkdownReport(m);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.PDF_EXPORT, () -> {
                PublishedReport p = pdfExportStage.export(state.markdownReport());
                return state.withPublishedReport(p);
            });
            state = exec(state, ResearchWorkflowState.ResearchStage.SEMANTIC_STORAGE, () -> {
                StoredResearchMemory m = semanticMemoryStage.store(state.publishedReport(), state.executiveSummary());
                return state.withStoredMemory(m);
            });
            log.info("=== WORKFLOW COMPLETED: {} ===", id);
        } catch (Exception e) {
            log.error("Workflow {} failed at {}: {}", id, state.currentStage(), e.getMessage());
            state = state.withError(e.getMessage());
        }
        workflows.put(id, state);
        return state;
    }

    public CompletableFuture<ResearchWorkflowState> executeAsync(String userPrompt) {
        return CompletableFuture.supplyAsync(() -> executeSync(userPrompt));
    }

    private ResearchWorkflowState exec(ResearchWorkflowState state, ResearchWorkflowState.ResearchStage stage,
                                        StageExecutor executor) {
        log.info("  Stage: {} [START]", stage);
        long start = System.currentTimeMillis();
        ResearchWorkflowState result = executor.execute();
        log.info("  Stage: {} [COMPLETE] ({}ms)", stage, System.currentTimeMillis() - start);
        workflows.put(state.workflowId(), result);
        return result;
    }

    public ResearchWorkflowState getWorkflow(String workflowId) { return workflows.get(workflowId); }

    @FunctionalInterface
    private interface StageExecutor { ResearchWorkflowState execute(); }
}

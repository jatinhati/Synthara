package dev.synthara.research.agents;

import com.embabel.agent.Agent;
import com.embabel.agent.annotation.Action;
import com.embabel.agent.annotation.AchievesGoal;
import com.embabel.agent.core.DefaultLlm;
import com.embabel.agent.exchange.UserInput;
import dev.synthara.research.records.*;
import dev.synthara.research.workflow.ResearchWorkflow;
import dev.synthara.research.workflow.ResearchWorkflowState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Agent(description = "Autonomous research analyst that performs web research, competitor analysis, SWOT analysis, generates professional reports, and exports to Markdown and PDF.")
public class ResearchAnalystAgent {

    private static final Logger log = LoggerFactory.getLogger(ResearchAnalystAgent.class);
    private final ResearchWorkflow workflow;

    public ResearchAnalystAgent(ResearchWorkflow workflow) {
        this.workflow = workflow;
    }

    @Action(inputType = UserInput.class, outputType = ResearchedTopic.class,
        systemPrompt = "Research the given topic thoroughly using web search tools. Return the researched topic with all gathered sources.",
        id = "research-topic")
    public ResearchedTopic researchTopic(UserInput userInput, DefaultLlm ai) {
        String prompt = userInput.value();
        log.info("Starting research for: {}", prompt);
        ResearchWorkflowState state = workflow.executeSync(prompt);
        return state.researchedTopic();
    }

    @Action(inputType = ResearchedTopic.class, outputType = ExecutiveSummary.class,
        systemPrompt = "Generate a comprehensive executive summary from the researched topic. Write for C-suite audience.",
        id = "research-summarize")
    @AchievesGoal
    public ExecutiveSummary analyzeAndReport(ResearchedTopic researched, DefaultLlm ai) {
        log.info("Generating executive summary for: {}", researched.topic());
        ResearchWorkflowState state = workflow.executeSync(researched.topic());
        if (state.publishedReport() != null)
            log.info("Report exported to: {}", state.publishedReport().markdownPath());
        return state.executiveSummary() != null ? state.executiveSummary()
            : new ExecutiveSummary(researched.topic(), "Analysis complete.", java.util.List.of(),
                java.util.List.of(), java.util.List.of(), java.util.List.of(), "", 50);
    }
}

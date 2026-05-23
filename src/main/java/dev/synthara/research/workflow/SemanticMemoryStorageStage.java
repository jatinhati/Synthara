package dev.synthara.research.workflow;

import dev.synthara.research.records.ExecutiveSummary;
import dev.synthara.research.records.PublishedReport;
import dev.synthara.research.records.StoredResearchMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class SemanticMemoryStorageStage {

    private static final Logger log = LoggerFactory.getLogger(SemanticMemoryStorageStage.class);

    public StoredResearchMemory store(PublishedReport publishedReport, ExecutiveSummary summary) {
        String memoryId = UUID.randomUUID().toString().substring(0, 12);
        log.info("Storing in semantic memory: id={}, topic={}", memoryId, summary.topic());
        return new StoredResearchMemory(memoryId, summary.topic(), summary.summary(),
            summary.majorFindings(), extractCompanies(summary.topic()), extractTechnologies(summary.topic()),
            publishedReport.markdownPath() != null ? publishedReport.markdownPath().toString() : "",
            List.of(), LocalDateTime.now(), 0);
    }

    private List<String> extractCompanies(String topic) {
        var c = new java.util.ArrayList<String>();
        String l = topic.toLowerCase();
        if (l.contains("microsoft") || l.contains("edge")) c.add("Microsoft");
        if (l.contains("opera")) c.add("Opera");
        if (l.contains("google") || l.contains("chrome")) c.add("Google");
        if (l.contains("brave")) c.add("Brave Software");
        if (l.contains("arc")) c.add("The Browser Company");
        if (l.contains("openai")) c.add("OpenAI");
        return c.isEmpty() ? List.of("Unknown") : List.copyOf(c);
    }

    private List<String> extractTechnologies(String topic) {
        var t = new java.util.ArrayList<String>();
        String l = topic.toLowerCase();
        if (l.contains("ai") || l.contains("llm")) t.add("AI/LLM");
        if (l.contains("browser") || l.contains("web")) t.add("Web Browser");
        if (l.contains("gpt")) t.add("GPT");
        if (l.contains("copilot")) t.add("Copilot");
        return t.isEmpty() ? List.of("General") : List.copyOf(t);
    }
}

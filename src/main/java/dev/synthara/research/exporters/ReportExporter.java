package dev.synthara.research.exporters;

import dev.synthara.research.records.*;
import org.springframework.stereotype.Component;

@Component
public class ReportExporter {

    public String summary(PublishedReport published) {
        if (published == null) return "No report published.";
        var sb = new StringBuilder("=== RESEARCH REPORT ===\n\n");
        sb.append("Topic: ").append(published.report().topic()).append("\n");
        sb.append("Markdown: ").append(published.markdownPath()).append("\n");
        sb.append("PDF: ").append(published.pdfPath()).append("\n");
        sb.append("PDF Generated: ").append(published.pdfGeneratedSuccessfully() ? "Yes" : "No (HTML available)").append("\n");
        return sb.toString();
    }
}

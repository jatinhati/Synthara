package dev.synthara.research.workflow;

import dev.synthara.research.records.MarkdownReport;
import dev.synthara.research.records.PublishedReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PdfExportStage {

    private static final Logger log = LoggerFactory.getLogger(PdfExportStage.class);

    public PublishedReport export(MarkdownReport report) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String safeTopic = report.topic().replaceAll("[^a-zA-Z0-9-_]", "-").toLowerCase();
        String dir = "reports";

        try {
            Path dirPath = Paths.get(dir);
            Files.createDirectories(dirPath);

            String mdFilename = safeTopic + "-report-" + date + ".md";
            Path mdPath = dirPath.resolve(mdFilename);
            Files.writeString(mdPath, report.fullReportMarkdown(), StandardCharsets.UTF_8);
            log.info("Report exported: {}", mdPath.toAbsolutePath());

            boolean pdfSuccess = false;
            Path pdfPath = null;
            try {
                String pdfFilename = safeTopic + "-report-" + date + ".pdf";
                ProcessBuilder pb = new ProcessBuilder(
                    "pandoc", mdPath.toString(), "-o", dirPath.resolve(pdfFilename).toString(),
                    "--pdf-engine=xelatex", "-V", "geometry:margin=1in");
                int exit = pb.start().waitFor();
                if (exit == 0) { pdfSuccess = true; pdfPath = dirPath.resolve(pdfFilename); }
            } catch (Exception e) {
                log.warn("PDF export not available (pandoc missing?). HTML/markdown created. {}", e.getMessage());
            }

            return new PublishedReport(report, mdPath.toAbsolutePath(), pdfPath,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), pdfSuccess);
        } catch (IOException e) {
            log.error("Export failed: {}", e.getMessage());
            return new PublishedReport(report, null, null,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), false);
        }
    }
}

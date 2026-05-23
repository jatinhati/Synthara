package dev.synthara.research.tools;

import com.embabel.agent.tool.LlmTool;
import com.embabel.agent.tool.LlmTool.Param;
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
public class ReportExportTool {

    private static final Logger log = LoggerFactory.getLogger(ReportExportTool.class);

    @LlmTool(description = "Exports a Markdown report to a file on disk. Creates the output directory if it does not exist. Returns the file path.")
    public String exportMarkdownReport(
        @Param(description = "The full Markdown content of the report") String markdownContent,
        @Param(description = "The topic or title for the filename") String topic,
        @Param(description = "The output directory path (default: ./reports)", required = false) String outputDir
    ) {
        String dir = outputDir != null ? outputDir : "./reports";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String safeTopic = topic != null ? topic.replaceAll("[^a-zA-Z0-9-_]", "-").toLowerCase() : "research-report";
        String filename = safeTopic + "-" + timestamp + ".md";

        try {
            Path outputPath = Paths.get(dir);
            Files.createDirectories(outputPath);
            Path filePath = outputPath.resolve(filename);
            Files.writeString(filePath, markdownContent, StandardCharsets.UTF_8);
            log.info("Report exported: {}", filePath.toAbsolutePath());
            return "EXPORTED: " + filePath.toAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to export report: {}", e.getMessage());
            return "EXPORT FAILED: " + e.getMessage();
        }
    }
}

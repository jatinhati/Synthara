package dev.synthara.research.workflow;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import dev.synthara.research.records.MarkdownReport;
import dev.synthara.research.records.PublishedReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfExportStage {

    private static final Logger log = LoggerFactory.getLogger(PdfExportStage.class);

    private static final String PDF_CSS = """
        @page {
            size: A4;
            margin: 2.5cm 2cm;
        }
        body {
            font-family: 'Helvetica', 'Arial', sans-serif;
            font-size: 11pt;
            line-height: 1.6;
            color: #1a1a2e;
            max-width: 100%;
        }
        h1 {
            font-size: 22pt;
            color: #0f3460;
            border-bottom: 3px solid #e94560;
            padding-bottom: 8px;
            margin-top: 0;
            margin-bottom: 16px;
        }
        h2 {
            font-size: 16pt;
            color: #16213e;
            border-bottom: 1px solid #cccccc;
            padding-bottom: 4px;
            margin-top: 24px;
            margin-bottom: 10px;
        }
        h3 {
            font-size: 13pt;
            color: #0f3460;
            margin-top: 18px;
            margin-bottom: 8px;
        }
        p {
            margin-bottom: 10px;
            text-align: justify;
        }
        strong {
            color: #16213e;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 16px 0;
            font-size: 9.5pt;
        }
        th {
            background-color: #0f3460;
            color: #ffffff;
            padding: 8px 10px;
            text-align: left;
            font-weight: bold;
        }
        td {
            padding: 7px 10px;
            border-bottom: 1px solid #dddddd;
        }
        tr:nth-child(even) td {
            background-color: #f4f4f8;
        }
        ul, ol {
            margin: 8px 0;
            padding-left: 24px;
        }
        li {
            margin-bottom: 4px;
        }
        hr {
            border: none;
            border-top: 2px solid #e94560;
            margin: 24px 0;
        }
        em {
            color: #555555;
        }
        .cover-meta {
            color: #555555;
            font-size: 10pt;
            margin-bottom: 20px;
        }
    """;

    public PublishedReport export(MarkdownReport report) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String safeTopic = report.topic().replaceAll("[^a-zA-Z0-9-_]", "-").toLowerCase();
        String dir = "reports";

        try {
            Path dirPath = Paths.get(dir);
            Files.createDirectories(dirPath);

            // 1. Write Markdown file
            String mdFilename = safeTopic + "-report-" + date + ".md";
            Path mdPath = dirPath.resolve(mdFilename);
            Files.writeString(mdPath, report.fullReportMarkdown(), StandardCharsets.UTF_8);
            log.info("Markdown report exported: {}", mdPath.toAbsolutePath());

            // 2. Convert Markdown → HTML
            String htmlContent = markdownToHtml(report.fullReportMarkdown());

            // 3. Wrap in styled HTML document
            String fullHtml = buildStyledHtmlDocument(htmlContent, report.topic());

            // 4. Convert HTML → PDF
            String pdfFilename = safeTopic + "-report-" + date + ".pdf";
            Path pdfPath = dirPath.resolve(pdfFilename);
            generatePdf(fullHtml, pdfPath);

            log.info("PDF report exported: {}", pdfPath.toAbsolutePath());

            return new PublishedReport(report, mdPath.toAbsolutePath(), pdfPath.toAbsolutePath(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);

        } catch (Exception e) {
            log.error("PDF export failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());

            // Still try to save the markdown
            try {
                Path dirPath = Paths.get(dir);
                Files.createDirectories(dirPath);
                String mdFilename = safeTopic + "-report-" + date + ".md";
                Path mdPath = dirPath.resolve(mdFilename);
                Files.writeString(mdPath, report.fullReportMarkdown(), StandardCharsets.UTF_8);
                log.info("Markdown-only report saved: {}", mdPath.toAbsolutePath());
                return new PublishedReport(report, mdPath.toAbsolutePath(), null,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), false);
            } catch (IOException ioe) {
                log.error("Even markdown export failed: {}", ioe.getMessage());
                return new PublishedReport(report, null, null,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), false);
            }
        }
    }

    private String markdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(TablesExtension.create()));
        options.set(HtmlRenderer.SOFT_BREAK, "<br/>\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    private String buildStyledHtmlDocument(String bodyHtml, String topic) {
        var sb = new StringBuilder();
        sb.append("<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>");
        sb.append(escapeHtml(topic));
        sb.append(" - Research Report</title>\n<style>\n");
        sb.append(PDF_CSS);
        sb.append("\n</style>\n</head>\n<body>\n");
        sb.append(bodyHtml);
        sb.append("\n</body>\n</html>");
        return sb.toString();
    }

    private void generatePdf(String html, Path outputPath) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPath.toFile())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            try { Files.deleteIfExists(outputPath); } catch (IOException ignored) {}
            throw e;
        }
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}

package dev.synthara.research.records;

import java.nio.file.Path;

public record PublishedReport(
    MarkdownReport report,
    Path markdownPath,
    Path pdfPath,
    String exportTimestamp,
    boolean pdfGeneratedSuccessfully
) {}

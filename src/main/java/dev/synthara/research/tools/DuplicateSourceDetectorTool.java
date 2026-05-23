package dev.synthara.research.tools;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.annotation.LlmTool.Param;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DuplicateSourceDetectorTool {

    @LlmTool(description = "Detects duplicate or near-duplicate sources from a list of research source URLs and titles.")
    public String detectDuplicates(
        @Param(description = "Sources in format: url|title, one per line") String sourcesText
    ) {
        if (sourcesText == null || sourcesText.isBlank()) return "DUPLICATE DETECTION: No sources";

        String[] lines = sourcesText.split("\n");
        Set<String> seenUrls = new HashSet<>();
        Set<String> seenTitles = new HashSet<>();
        var duplicates = new ArrayList<String>();
        var unique = new ArrayList<String>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\|", 2);
            String url = parts.length > 0 ? parts[0].trim() : "";
            String title = parts.length > 1 ? parts[1].trim() : "";

            boolean isDup = (!url.isEmpty() && !seenUrls.add(url.toLowerCase()))
                || (!title.isEmpty() && !seenTitles.add(title.toLowerCase().replaceAll("[^a-z0-9]", "")));

            if (isDup) duplicates.add(line);
            else unique.add(line);
        }

        var sb = new StringBuilder();
        sb.append("DUPLICATE DETECTION\n").append("=" .repeat(40)).append("\n\n");
        sb.append("Total: ").append(lines.length).append("\n");
        sb.append("Unique: ").append(unique.size()).append("\n");
        sb.append("Duplicates: ").append(duplicates.size()).append("\n\n");

        if (!duplicates.isEmpty()) {
            sb.append("DUPLICATES:\n");
            duplicates.forEach(d -> sb.append("  ✗ ").append(d).append("\n"));
        } else {
            sb.append("✓ No duplicates detected\n");
        }
        return sb.toString();
    }

    @LlmTool(description = "Calculates text similarity between two strings using Jaccard coefficient.")
    public String calculateSimilarity(
        @Param(description = "First text") String text1,
        @Param(description = "Second text") String text2
    ) {
        if (text1 == null || text2 == null || text1.isBlank() || text2.isBlank())
            return "SIMILARITY: 0%";

        Set<String> w1 = new HashSet<>(Arrays.asList(text1.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+")));
        Set<String> w2 = new HashSet<>(Arrays.asList(text2.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+")));

        var intersection = new HashSet<>(w1);
        intersection.retainAll(w2);
        double jaccard = (double) intersection.size() / (double) (w1.size() + w2.size() - intersection.size());
        int pct = (int) Math.round(jaccard * 100);

        return String.format("SIMILARITY: %d%%\nVerdict: %s",
            pct, pct >= 80 ? "Likely duplicate" : pct >= 50 ? "Similar topics" : "Different content");
    }
}

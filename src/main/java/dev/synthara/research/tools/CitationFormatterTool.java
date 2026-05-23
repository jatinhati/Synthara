package dev.synthara.research.tools;

import com.embabel.agent.tool.LlmTool;
import com.embabel.agent.tool.LlmTool.Param;
import org.springframework.stereotype.Component;

@Component
public class CitationFormatterTool {

    @LlmTool(description = "Formats research sources into properly styled APA/MLA style citations for inclusion in reports.")
    public String formatCitations(
        @Param(description = "JSON array of sources with title, url, publicationDate, author fields") String sourcesJson,
        @Param(description = "Citation style: 'apa' (default) or 'mla'", required = false) String style
    ) {
        if (sourcesJson == null || sourcesJson.isBlank()) return "No sources to format";

        String styleLower = style != null ? style.toLowerCase() : "apa";
        var sb = new StringBuilder("## References\n\n");
        int count = 0;

        for (String line : sourcesJson.split("\n")) {
            if (line.contains("\"title\"")) {
                count++;
                String title = extract(line, "title");
                String url = extract(line, "url");
                String author = extract(line, "author");

                if ("mla".equals(styleLower)) {
                    sb.append(count).append(". ");
                    if (author != null) sb.append(author).append(". ");
                    sb.append("\"").append(title).append(".\" ");
                    if (url != null) sb.append(url).append(".");
                } else {
                    sb.append(count).append(". ");
                    if (author != null) sb.append(author).append(" ");
                    sb.append("*").append(title).append("*");
                    if (url != null) sb.append(". ").append(url);
                    sb.append(".");
                }
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    @LlmTool(description = "Generates inline citation markers [1], [2] for use within report text.")
    public String generateInlineCitations(
        @Param(description = "Comma-separated source identifiers in order of appearance") String sourceIdentifiers
    ) {
        if (sourceIdentifiers == null || sourceIdentifiers.isBlank()) return "";
        String[] sources = sourceIdentifiers.split(",");
        var sb = new StringBuilder();
        for (int i = 0; i < sources.length; i++) sb.append("[").append(i + 1).append("]");
        sb.append("\n\n### References\n\n");
        for (int i = 0; i < sources.length; i++)
            sb.append("[").append(i + 1).append("] ").append(sources[i].trim()).append("\n");
        return sb.toString();
    }

    private String extract(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) { search = "\"" + field + "\": \""; start = json.indexOf(search); }
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }
}

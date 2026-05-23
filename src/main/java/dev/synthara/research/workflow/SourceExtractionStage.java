package dev.synthara.research.workflow;

import dev.synthara.research.records.ResearchSource;
import dev.synthara.research.records.ResearchedTopic;
import dev.synthara.research.records.StructuredSources;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SourceExtractionStage {

    public StructuredSources extract(ResearchedTopic researched) {
        var allSources = new ArrayList<>(researched.sources());
        var duplicates = new HashSet<String>();
        var lowQuality = new ArrayList<ResearchSource>();
        Set<String> seenUrls = new HashSet<>();
        Set<String> seenTitles = new HashSet<>();

        var filtered = allSources.stream()
            .filter(s -> {
                boolean isDup = !seenUrls.add(s.url().toLowerCase())
                    || !seenTitles.add(s.title().toLowerCase().replaceAll("[^a-z0-9]", ""));
                if (isDup) { duplicates.add(s.title()); return false; }
                if (s.credibilityScore() < 30) { lowQuality.add(s); return false; }
                return true;
            })
            .sorted(Comparator.comparingInt(ResearchSource::credibilityScore).reversed())
            .collect(Collectors.toList());

        var highQuality = filtered.stream().filter(s -> s.credibilityScore() >= 70).collect(Collectors.toList());

        return new StructuredSources(researched.topic(), List.copyOf(filtered), List.copyOf(highQuality),
            List.copyOf(filtered), filtered.size(), duplicates.size(), lowQuality.size());
    }
}

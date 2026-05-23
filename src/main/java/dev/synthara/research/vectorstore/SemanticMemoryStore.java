package dev.synthara.research.vectorstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SemanticMemoryStore {

    private static final Logger log = LoggerFactory.getLogger(SemanticMemoryStore.class);
    private final Map<String, StoredResearch> store = new LinkedHashMap<>();

    public String store(String topic, String summary, List<String> findings, List<String> companies, String reportPath) {
        String id = UUID.randomUUID().toString().substring(0, 12);
        store.put(id, new StoredResearch(id, topic, summary, findings, companies, reportPath, new Date()));
        log.info("Stored: {} ({})", id, topic);
        return id;
    }

    public Optional<StoredResearch> findByTopic(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
            .filter(s -> s.topic().toLowerCase().contains(q) || s.summary().toLowerCase().contains(q)
                || s.companies().stream().anyMatch(c -> c.toLowerCase().contains(q)))
            .findFirst();
    }

    public List<StoredResearch> search(String query, int max) {
        String q = query.toLowerCase();
        return store.values().stream()
            .filter(s -> s.topic().toLowerCase().contains(q) || s.summary().toLowerCase().contains(q))
            .limit(max).toList();
    }

    public int size() { return store.size(); }

    public record StoredResearch(String id, String topic, String summary, List<String> findings,
                                  List<String> companies, String reportPath, Date storedAt) {}
}

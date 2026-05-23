package dev.synthara.research.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class TavilySearchService {
    private static final Logger log = LoggerFactory.getLogger(TavilySearchService.class);
    private final RestClient restClient;
    private final String apiKey;

    public TavilySearchService(
            @Value("${spring.ai.mcp.client.stdio.connections.tavily-search-mcp.env.TAVILY_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl("https://api.tavily.com").build();
        log.info("TavilySearchService initialized with API key: {}...", apiKey.substring(0, Math.min(10, apiKey.length())));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> search(String query) {
        log.info("Executing live Tavily Search: {}", query);
        try {
            Map<String, Object> request = Map.of(
                "api_key", apiKey,
                "query", query,
                "search_depth", "basic",
                "max_results", 5
            );

            Map<String, Object> response = restClient.post()
                .uri("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                log.info("Tavily returned {} results", results.size());
                return results;
            }
            log.warn("Tavily response had no 'results' key. Response keys: {}", response != null ? response.keySet() : "null");
        } catch (Exception e) {
            log.error("Tavily search failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return List.of();
    }
}


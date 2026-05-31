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
public class OpenRouterLlmService {
    private static final Logger log = LoggerFactory.getLogger(OpenRouterLlmService.class);
    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;
    private final String modelName;

    public OpenRouterLlmService(
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.chat.options.model}") String modelName) {
        this.apiKey = apiKey;
        this.modelName = modelName;

        this.apiUrl = buildApiUrl(baseUrl);

        this.restClient = RestClient.builder().build();
        log.info("NVIDIA NIM LLM service initialized: url={}, model={}", this.apiUrl, this.modelName);
    }

    private String buildApiUrl(String baseUrl) {
        // Already a full endpoint path — use as-is
        if (baseUrl.endsWith("/api/chat") || baseUrl.endsWith("/v1/chat/completions") || baseUrl.endsWith("/chat/completions")) {
            return baseUrl;
        }
        // Ollama-style /api base
        if (baseUrl.endsWith("/api")) {
            return baseUrl + "/chat";
        }
        // Base already includes /v1 (e.g. https://api.openai.com/v1)
        if (baseUrl.endsWith("/v1")) {
            return baseUrl + "/chat/completions";
        }
        // Bare domain/host (e.g. https://integrate.api.nvidia.com) → full OpenAI-compatible path
        return baseUrl + "/v1/chat/completions";
    }

    @SuppressWarnings("unchecked")
    public String call(String prompt) {
        log.info("Calling NVIDIA NIM LLM [model={}] with prompt ({} chars)", modelName, prompt.length());
        log.debug("Prompt (first 200 chars): {}", prompt.substring(0, Math.min(200, prompt.length())));
        try {
            Map<String, Object> request = new java.util.HashMap<>();
            request.put("model", modelName);
            request.put("stream", false);
            request.put("max_tokens", 16384);
            request.put("temperature", 1.0);
            request.put("top_p", 0.95);
            request.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));

            RestClient.RequestBodySpec requestSpec = restClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON);

            if (apiKey != null && !apiKey.isBlank()) {
                requestSpec = requestSpec.header("Authorization", "Bearer " + apiKey);
            }

            Map<String, Object> response = requestSpec
                .body(request)
                .retrieve()
                .body(Map.class);

            log.info("LLM response received: {}", response != null ? response.keySet() : "null");

            if (response != null && response.containsKey("error")) {
                log.error("LLM API error: {}", response.get("error"));
                return null;
            }

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    if (message != null) {
                        String content = (String) message.get("content");
                        log.info("LLM response length: {} chars", content != null ? content.length() : 0);
                        return content;
                    }
                }
            }

            if (response != null && response.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) response.get("message");
                if (message != null) {
                    String content = (String) message.get("content");
                    log.info("LLM response length: {} chars", content != null ? content.length() : 0);
                    return content;
                }
            }

            if (response != null && response.containsKey("response")) {
                String content = (String) response.get("response");
                log.info("LLM response length: {} chars", content != null ? content.length() : 0);
                return content;
            }

            log.warn("LLM response had no recognizable content. Full response: {}", response);
        } catch (Exception e) {
            log.error("LLM API call failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }
}

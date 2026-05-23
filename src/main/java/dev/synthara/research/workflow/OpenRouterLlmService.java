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

        // Build the completions URL once
        if (baseUrl.endsWith("/api")) {
            this.apiUrl = baseUrl + "/v1/chat/completions";
        } else if (!baseUrl.endsWith("/v1/chat/completions")) {
            this.apiUrl = baseUrl + "/chat/completions";
        } else {
            this.apiUrl = baseUrl;
        }

        this.restClient = RestClient.builder().build();
        log.info("OpenRouterLlmService initialized: url={}, model={}", this.apiUrl, this.modelName);
    }

    @SuppressWarnings("unchecked")
    public String call(String prompt) {
        log.info("Calling OpenRouter LLM [model={}] with prompt ({} chars)", modelName, prompt.length());
        log.debug("Prompt (first 200 chars): {}", prompt.substring(0, Math.min(200, prompt.length())));
        try {
            Map<String, Object> request = Map.of(
                "model", modelName,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                )
            );

            Map<String, Object> response = restClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "https://synthara.dev")
                .header("X-Title", "Synthara Research")
                .body(request)
                .retrieve()
                .body(Map.class);

            log.info("OpenRouter response received: {}", response != null ? response.keySet() : "null");

            if (response != null && response.containsKey("error")) {
                log.error("OpenRouter API error: {}", response.get("error"));
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

            log.warn("OpenRouter response had no choices. Full response: {}", response);
        } catch (Exception e) {
            log.error("OpenRouter API call failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                log.error("API key may be invalid or expired. Check spring.ai.openai.api-key in application.yaml");
            }
        }
        return null;
    }
}

package com.codemorph.backend.ai.service;


//package com.codemorph.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaLlmProvider implements LlmProvider {

    private final RestClient client;
    private final ObjectMapper mapper;
    private final String model;

    public OllamaLlmProvider(
            RestClient.Builder builder,
            ObjectMapper mapper,
            @Value("${codemorph.llm.ollama.base-url}") String baseUrl,
            @Value("${codemorph.llm.ollama.model}") String model) {

        this.client = builder.baseUrl(baseUrl).build();
        this.mapper = mapper;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content",
                                "You are CodeMorph's software migration assistant. " +
                                        "Explain supplied analysis accurately. " +
                                        "Never invent project metrics."
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "stream", false,
                "options", Map.of(
                        "temperature", 0.2
                )
        );

        String response = client.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode content = mapper.readTree(response)
                    .path("message")
                    .path("content");

            if (content.isMissingNode() || content.isNull()) {
                throw new IllegalStateException(
                        "Ollama returned an empty answer"
                );
            }

            return content.asText();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not read the Ollama response",
                    exception
            );
        }
    }

    @Override
    public String getProviderName() {
        return "OLLAMA";
    }
}
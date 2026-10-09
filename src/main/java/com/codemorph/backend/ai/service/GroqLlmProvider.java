package com.codemorph.backend.ai.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqLlmProvider implements LlmProvider {

    private final RestClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String model;

    public GroqLlmProvider(
            RestClient.Builder builder,
            ObjectMapper mapper,
            @Value("${codemorph.llm.groq.base-url}") String baseUrl,
            @Value("${codemorph.llm.groq.api-key:}") String apiKey,
            @Value("${codemorph.llm.groq.model}") String model) {

        this.client = builder.baseUrl(baseUrl).build();
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GROQ_API_KEY is not configured"
            );
        }

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
                "temperature", 0.2
        );

        String response = client.post()
                .uri("/chat/completions")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode content = mapper.readTree(response)
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            if (content.isMissingNode() || content.isNull()) {
                throw new IllegalStateException(
                        "Groq returned an empty answer"
                );
            }

            return content.asText();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not read the Groq response",
                    exception
            );
        }
    }

    @Override
    public String getProviderName() {
        return "GROQ";
    }
}
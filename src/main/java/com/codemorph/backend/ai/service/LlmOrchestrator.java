package com.codemorph.backend.ai.service;


//package com.codemorph.ai.service;

import com.codemorph.backend.ai.dto.LlmChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LlmOrchestrator {

    private static final Logger log =
            LoggerFactory.getLogger(LlmOrchestrator.class);

    private final GroqLlmProvider groq;
    private final OllamaLlmProvider ollama;

    public LlmOrchestrator(
            GroqLlmProvider groq,
            OllamaLlmProvider ollama) {
        this.groq = groq;
        this.ollama = ollama;
    }

    public LlmChatResponse generate(String prompt) {

        // First, try Groq.
        try {
            String answer = groq.generate(prompt);

            return new LlmChatResponse(
                    answer,
                    "GROQ",
                    false
            );

        } catch (Exception groqError) {

            log.warn(
                    "Groq failed; trying Ollama. Error type: {}",
                    groqError.getClass().getSimpleName()
            );
        }

        // If Groq fails, try local Ollama.
        try {
            String answer = ollama.generate(prompt);

            return new LlmChatResponse(
                    answer,
                    "OLLAMA",
                    true
            );

        } catch (Exception ollamaError) {

            log.error("Both Groq and Ollama failed.");

            throw new IllegalStateException(
                    "Both AI providers are unavailable.",
                    ollamaError
            );
        }
    }
}
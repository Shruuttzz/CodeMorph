package com.codemorph.backend.controller;


//package com.codemorph.ai.controller;

import com.codemorph.backend.ai.dto.LlmChatRequest;
import com.codemorph.backend.ai.dto.LlmChatResponse;
import com.codemorph.backend.ai.service.CodeMorphPromptBuilder;
import com.codemorph.backend.ai.service.LlmOrchestrator;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class LlmController {

    private final LlmOrchestrator orchestrator;
    private final CodeMorphPromptBuilder promptBuilder;

    public LlmController(
            LlmOrchestrator orchestrator,
            CodeMorphPromptBuilder promptBuilder
    ) {
        this.orchestrator = orchestrator;
        this.promptBuilder = promptBuilder;
    }

    @PostMapping("/chat")
    public ResponseEntity<LlmChatResponse> chat(
            @Valid @RequestBody LlmChatRequest request
    ) {
        String prompt = promptBuilder.build(request);

        LlmChatResponse response =
                orchestrator.generate(prompt);

        return ResponseEntity.ok(response);
    }
}
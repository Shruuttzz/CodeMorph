package com.codemorph.backend.ai.dto;


//package com.codemorph.ai.dto;

public record LlmChatResponse(
        String answer,
        String provider,
        boolean fallbackUsed
) {
}
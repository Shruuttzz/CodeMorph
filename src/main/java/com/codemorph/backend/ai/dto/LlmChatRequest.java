package com.codemorph.backend.ai.dto;
//package com.codemorph.backend.ai.dto

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LlmChatRequest(

        @NotBlank(message = "Message cannot be empty")
        @Size(max = 10000, message = "Message cannot exceed 10000 characters")
        String message,

        @Size(max = 50000, message = "Context cannot exceed 50000 characters")
        String context

) {
}

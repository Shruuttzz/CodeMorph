package com.codemorph.backend.ai.service;


public interface LlmProvider {

    String generate(String prompt);

    String getProviderName();
}

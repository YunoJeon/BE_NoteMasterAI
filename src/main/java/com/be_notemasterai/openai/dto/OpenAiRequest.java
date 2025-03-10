package com.be_notemasterai.openai.dto;

import com.be_notemasterai.config.OpenAiConfig;
import java.util.List;
import java.util.Map;

public record OpenAiRequest(String model, List<Map<String, String>> messages) {

  public static OpenAiRequest of(OpenAiConfig openAiConfig, String prompt) {

    return new OpenAiRequest(openAiConfig.getModel(),
        List.of(Map.of("role", "user", "content", prompt)));
  }
}
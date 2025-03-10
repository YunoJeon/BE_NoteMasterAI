package com.be_notemasterai.openai.dto;

import java.util.List;
import java.util.Map;

public record OpenAiResponse(List<Choice> choices) {

  public record Choice(Map<String, String> message) {
  }
}
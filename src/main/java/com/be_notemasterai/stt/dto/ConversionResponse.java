package com.be_notemasterai.stt.dto;

import java.util.List;

public record ConversionResponse(List<Dialogue> dialogues) {

  public record Dialogue(String speaker, String text) {}
}
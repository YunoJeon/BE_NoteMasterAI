package com.be_notemasterai.stt.dto;

public record ClovaRequest(String language, String completion, Diarization diarization) {

  public static ClovaRequest createDefault() {
    return new ClovaRequest("ko-KR", "sync", new Diarization(true));
  }
  public record Diarization(boolean enable) {}
}

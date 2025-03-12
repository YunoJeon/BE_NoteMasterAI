package com.be_notemasterai.stt.util;

import static com.be_notemasterai.exception.ErrorCode.FAILED_STT_PARSING;

import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.stt.dto.ConversionResponse;
import com.be_notemasterai.stt.dto.ConversionResponse.Dialogue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClovaResponseParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static ConversionResponse parseClovaResponse(String jsonResponse) {

    try {

      JsonNode root = objectMapper.readTree(jsonResponse);
      JsonNode segments = root.path("segments");

      List<Dialogue> dialogues = new ArrayList<>();

      Iterator<JsonNode> iterator = segments.elements();
      while (iterator.hasNext()) {
        JsonNode segment = iterator.next();
        String speaker = segment.path("speaker").path("label").asText();
        String text = segment.path("text").asText();

        dialogues.add(new Dialogue(speaker, text));
      }
      return new ConversionResponse(dialogues);

    } catch (Exception e) {
      throw new CustomException(FAILED_STT_PARSING);
    }
  }
}
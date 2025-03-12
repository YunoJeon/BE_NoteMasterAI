package com.be_notemasterai.stt.service;

import static com.be_notemasterai.exception.ErrorCode.EMPTY_SOUND_FILE;
import static com.be_notemasterai.exception.ErrorCode.FAILED_TO_STT;
import static com.be_notemasterai.exception.ErrorCode.NOT_SUBSCRIBER;
import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;

import com.be_notemasterai.config.ClovaConfig;
import com.be_notemasterai.exception.CustomException;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.stt.client.ClovaSttClient;
import com.be_notemasterai.stt.dto.ClovaRequest;
import com.be_notemasterai.stt.dto.ConversionResponse;
import com.be_notemasterai.stt.util.ClovaResponseParser;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SpeechToTextService {

  private final ClovaSttClient clovaSttClient;

  private final ClovaConfig clovaConfig;

  private final SubscribeRepository subscribeRepository;

  private final ObjectMapper objectMapper;

  public ConversionResponse transcribeSpeech(Member member, MultipartFile file) {

    if (file.isEmpty()) {
      throw new CustomException(EMPTY_SOUND_FILE);
    }

    if (!subscribeRepository.existsBySubscriberIdAndSubscriptionStatus(member.getId(), ACTIVE)) {
      throw new CustomException(NOT_SUBSCRIBER);
    }

    try {

      ClovaRequest request = ClovaRequest.createDefault();
      String paramJson = objectMapper.writeValueAsString(request);

      String response = clovaSttClient.uploadSpeechFIle(clovaConfig.getSecret(), file, paramJson);

      return ClovaResponseParser.parseClovaResponse(response);
    } catch (Exception e) {
      throw new CustomException(FAILED_TO_STT);
    }
  }
}
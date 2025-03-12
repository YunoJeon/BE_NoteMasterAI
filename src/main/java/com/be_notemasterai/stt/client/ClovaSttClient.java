package com.be_notemasterai.stt.client;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "clova-stt", url = "${naver.cloud.clova.stt.invoke_url}")
public interface ClovaSttClient {

  @PostMapping(value = "/recognizer/upload", consumes = MULTIPART_FORM_DATA_VALUE)
  String uploadSpeechFIle(
      @RequestHeader("X-CLOVASPEECH-API-KEY") String apiKey,
      @RequestPart("media") MultipartFile file,
      @RequestPart("params") String params);
}
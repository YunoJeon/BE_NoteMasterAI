package com.be_notemasterai.stt.controller;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.security.resolver.CurrentMember;
import com.be_notemasterai.stt.dto.ConversionResponse;
import com.be_notemasterai.stt.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stt")
public class SpeechToTextController {

  private final SpeechToTextService speechToTextService;

  @PostMapping("/upload")
  public ResponseEntity<ConversionResponse> uploadAudioFile(
      @RequestParam("file") MultipartFile file, @CurrentMember Member member) {

    ConversionResponse response = speechToTextService.transcribeSpeech(member, file);

    return ResponseEntity.ok(response);
  }
}
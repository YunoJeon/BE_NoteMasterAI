package com.be_notemasterai.openai.controller;

import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.openai.dto.SummaryRequest;
import com.be_notemasterai.openai.dto.SummaryResponse;
import com.be_notemasterai.openai.service.OpenAiService;
import com.be_notemasterai.security.resolver.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class AiSummaryController {

  private final OpenAiService openAiService;

  @PostMapping
  public ResponseEntity<SummaryResponse> summarize(
      @RequestBody SummaryRequest summaryRequest,
      @CurrentMember Member member) {

    SummaryResponse summaryResponse = openAiService.summarize(summaryRequest, member);

    return ResponseEntity.ok(summaryResponse);
  }
}
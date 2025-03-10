package com.be_notemasterai.openai.client;

import com.be_notemasterai.openai.dto.OpenAiRequest;
import com.be_notemasterai.openai.dto.OpenAiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "openAiClient", url = "${openai.base-url}")
public interface OpenAiClient {

  @PostMapping
  OpenAiResponse getSummary(@RequestHeader("Authorization") String apiKey,
      @RequestBody OpenAiRequest request);
}
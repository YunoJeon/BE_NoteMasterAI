package com.be_notemasterai.openai.service;

import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.be_notemasterai.config.OpenAiConfig;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.service.NoteService;
import com.be_notemasterai.openai.client.OpenAiClient;
import com.be_notemasterai.openai.dto.OpenAiRequest;
import com.be_notemasterai.openai.dto.OpenAiResponse;
import com.be_notemasterai.openai.dto.OpenAiResponse.Choice;
import com.be_notemasterai.openai.dto.SummaryRequest;
import com.be_notemasterai.openai.dto.SummaryResponse;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

  @Mock
  private OpenAiClient openAiClient;

  @Mock
  private OpenAiConfig openAiConfig;

  @Mock
  private SubscribeRepository subscribeRepository;

  @Mock
  private NoteService noteService;

  @InjectMocks
  private OpenAiService openAiService;

  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .provider("google")
        .providerUuid("providerUuid")
        .name("이름")
        .profileImageUrl("프로필이미지")
        .tag("태그")
        .build();
  }

  @Test
  @DisplayName("AI 요약이 정상적으로 성공한다")
  void summarize_success() {
    // given
    SummaryRequest request = new SummaryRequest("원본");
    OpenAiResponse openAiResponse = new OpenAiResponse(List.of(new Choice(Map.of("content", "요약"))));

    when(openAiConfig.getModel()).thenReturn("gpt-4o-mini");
    when(openAiClient.getSummary(anyString(), any(OpenAiRequest.class))).thenReturn(openAiResponse);
    when(subscribeRepository.existsBySubscriberIdAndSubscriptionStatus(member.getId(), ACTIVE)).thenReturn(true);
    // when
    SummaryResponse response = openAiService.summarize(request, member);
    // then
    assertEquals("요약", response.summaryResponse());
  }
}
package com.be_notemasterai.openai.service;

import static com.be_notemasterai.subscribe.type.SubscriptionStatus.ACTIVE;
import static java.util.regex.Pattern.MULTILINE;

import com.be_notemasterai.config.OpenAiConfig;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.service.NoteService;
import com.be_notemasterai.openai.client.OpenAiClient;
import com.be_notemasterai.openai.dto.OpenAiRequest;
import com.be_notemasterai.openai.dto.OpenAiResponse;
import com.be_notemasterai.openai.dto.SummaryRequest;
import com.be_notemasterai.openai.dto.SummaryResponse;
import com.be_notemasterai.subscribe.repository.SubscribeRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAiService {

  private final OpenAiClient openAiClient;

  private final OpenAiConfig openAiConfig;

  private final SubscribeRepository subscribeRepository;

  private final NoteService noteService;

  @Transactional
  public SummaryResponse summarize(SummaryRequest summaryRequest, Member member) {

    boolean isSubscriber = checkSubscriptionStatus(member);

    String prompt = generatePrompt(isSubscriber, summaryRequest.text());

    String summary = requestOpenAiSummary(prompt);

    String generatedTitle = extractTitle(summary);

    noteService.createNote(member, generatedTitle, summaryRequest.text(), summary);

    return new SummaryResponse(summary);
  }

  private boolean checkSubscriptionStatus(Member member) {
    return subscribeRepository.existsBySubscriberIdAndSubscriptionStatus(member.getId(), ACTIVE);
  }

  private String generatePrompt(boolean isSubscriber, String text) {
    return getPromptBasedOnPlan(isSubscriber, text);
  }

  private String getPromptBasedOnPlan(boolean isSubscriber, String text) {

    if (isSubscriber) {

      return """
          ### 역할
          1. 너는 GPT 4o 모델 스타일로 답변하는 AI 기반 문서 분석 전문가야.
          2. 다음 문서를 읽고, 논리적으로 구조화된 요약을 생성해줘.
          3. 답변할 때 무조건 적절한 이모지를 풍부하고 자주 사용해서 독자가 쉽게 이해할 수 있도록 답변해줘.
          4. 답변의 형식은 반드시 Markdown 형식으로 제공해야 돼.

          ### 요약 목표
          1. 각 섹션별 상세 요약(문서 내용의 핵심을 충분히 포함)
          2. 숫자, 통계, 날짜, 주요 정보 등을 강조
          3. 독자가 쉽게 이해할 수 있도록 요약문을 정리
          4. 필요한 경우 표 형식으로 정리
          5. 논리적이고 직관적으로 알기 쉽게 정리

          ### 요약 포맷
          1. **제목**
          2. 📌 **전체 정리**: %s
          3. 🔍 **결론**
          4. ✅ **포인트 정리**
          5. 📖 **단락별 요약**
          """.formatted(text);
    } else {

      return """
          너는 GPT 4o 모델 스타일로 답변하는 AI 기반 문서 분석 전문가야.
          다음 문서를 읽고 짧고 간결한 요약을 제공해줘.

          ### 요약 목표
          1. 문서의 핵심만 간략하게 정리 (1~3문장 이내)
          2. 불필요한 내용은 제외하고 본질적인 정보만 제공
          3. 독자가 빠르게 이해할 수 있도록 쉬운 문장으로 작성

          ### 요약 포맷
          1. **제목**
          2. **한 줄 요약**: %s
          3. **핵심 정보**: 중요한 숫자, 날짜, 키워드만 정리
          """.formatted(text);
    }
  }

  private String requestOpenAiSummary(String prompt) {

    OpenAiRequest openAiRequest = OpenAiRequest.of(openAiConfig, prompt);

    OpenAiResponse openAiResponse = openAiClient.getSummary("Bearer " + openAiConfig.getApiKey(),
        openAiRequest);

    return openAiResponse.choices().get(0).message().get("content");
  }

  private String extractTitle(String summary) {

    Pattern pattern = Pattern.compile("^(?:1\\.\\s\\*\\*(.*?)\\*\\*|#\\s*(.+?)\\n|##\\s*(.+?)\\n)",
        MULTILINE);
    Matcher matcher = pattern.matcher(summary);

    if (matcher.find()) {
      if (matcher.group(1) != null) {
        return matcher.group(1).trim();
      } else if (matcher.group(2) != null) {
        return matcher.group(2).trim();
      } else if (matcher.group(3) != null) {
        return matcher.group(3).trim();
      }
    }
    return "자동 생성된 제목";
  }
}
package com.freedom.news.infra.client;

import com.freedom.news.infra.client.response.SummaryResponse;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiNewsSummaryClient {
    private static final int LIMIT = 150;
    private final OpenAIClient openAIClient;

    public String summarize(String plainText) {
        String prompt = """
            당신은 한국어 뉴스 요약 전문가입니다.
            아래 뉴스 본문을 읽고 조건을 반드시 지켜 1문장 요약을 작성하세요.

            [조건]
            - 본문에 없는 내용을 추가하거나 수정하지 마세요.
            - 뉴스의 핵심 포인트를 한눈에 들어오게 작성하세요.
            - 핵심 주체/행위/수치/날짜가 있으면 포함하세요.
            - 기사 본문에 나온 사실만 사용하세요.
            - 본문에 없는 정보·추측·해석·평가·의견·의역·보정 금지합니다.
            - 출력은 오직 JSON: {"summary":"..."} (앞뒤 설명, 개행, 코드블록 없음)

            [뉴스 본문]
            %s
            """.formatted(plainText);

        StructuredChatCompletionCreateParams<SummaryResponse> params =
                StructuredChatCompletionCreateParams.<SummaryResponse>builder()
                        .model(ChatModel.GPT_4_1)
                        .temperature(0.2)
                        .maxCompletionTokens(220)
                        .responseFormat(SummaryResponse.class)
                        .addUserMessage(prompt)
                        .build();

        return openAIClient.chat()
                .completions()
                .create(params)
                .choices()
                .stream()
                .flatMap(choice -> choice.message().content().stream())
                .map(SummaryResponse::getSummary)
                .findFirst()
                .orElse("");
    }

}

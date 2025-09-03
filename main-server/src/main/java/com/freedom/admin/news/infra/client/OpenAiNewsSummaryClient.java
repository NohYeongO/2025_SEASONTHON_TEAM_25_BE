package com.freedom.admin.news.infra.client;

import com.freedom.admin.news.infra.client.response.ClassifiedSummaryResponse;
import com.freedom.admin.news.infra.client.response.SummaryResponse;
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

    public ClassifiedSummaryResponse classifyAndSummarize(String plainText) {
        String prompt = """
            너는 한국 경제 뉴스 필터 및 요약기이다. 아래 본문이 '경제 관련'인지 먼저 판별하고,
            경제 관련이라면 1문장 요약을, 아니라면 이유를 제시하라.

            [기준]
            - 경제 관련: 금리/물가/환율/주식/채권/부동산/정책/세금/고용/산업/기업실적/무역/금융 등 경제 활동과 직접 관련
            - 비경제: 사건사고/연예/스포츠/일상/순수 과학기술 동향 등 직접 경제성과가 불분명한 경우

            [출력(JSON만)]
            {"is_economic": true|false, "summary": "경제 기사면 1문장 요약, 아니면 빈 문자열", "reason": "비경제 판단 사유(선택)"}

            [뉴스 본문]
            %s
            """.formatted(plainText);

        StructuredChatCompletionCreateParams<ClassifiedSummaryResponse> params =
                StructuredChatCompletionCreateParams.<ClassifiedSummaryResponse>builder()
                        .model(ChatModel.GPT_4_1)
                        .temperature(0.2)
                        .maxCompletionTokens(280)
                        .responseFormat(ClassifiedSummaryResponse.class)
                        .addUserMessage(prompt)
                        .build();

        return openAIClient.chat()
                .completions()
                .create(params)
                .choices()
                .stream()
                .flatMap(choice -> choice.message().content().stream())
                .findFirst()
                .orElseGet(() -> {
                    ClassifiedSummaryResponse r = new ClassifiedSummaryResponse();
                    return r;
                });
    }
}

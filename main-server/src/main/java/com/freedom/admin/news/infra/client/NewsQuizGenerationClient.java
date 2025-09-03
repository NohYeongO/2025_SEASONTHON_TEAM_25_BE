package com.freedom.admin.news.infra.client;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsQuizGenerationClient {

    public record OxQuiz(String question, boolean answer, String explanation) {}
    public record McqOption(String text, boolean correct) {}
    public record McqQuiz(String question, List<McqOption> options, String explanation) {}
    public record QuizPack(OxQuiz ox, McqQuiz mcq, String category) {}

    private final OpenAIClient openAIClient;

    public QuizPack generateQuizzes(String title, String summary, String plainText) {
        String prompt = """
            너는 한국어 퀴즈 제작자이다. 아래 뉴스 제목/요약/본문을 바탕으로 OX 1문항, 4지선다 1문항을 만들어라.

            [요구사항]
            - 사실 검증 가능한 내용만 문제화
            - OX 문제는 명확히 참/거짓 판단 가능하도록 출제
            - 4지선다는 보기 4개, 오직 1개만 정답, 보기 길이/형태 균형
            - 각 문항마다 해설을 1-2문장 제공
            - 카테고리는 고정 문자열 "뉴스 기사"
            - 출력(JSON만):
              {
                "ox": {"question":"...","answer":true|false,"explanation":"..."},
                "mcq": {"question":"...","options":[{"text":"...","correct":true|false},...4개],"explanation":"..."},
                "category":"뉴스 기사"
              }

            [제목] %s
            [요약] %s
            [본문] %s
            """.formatted(title, summary == null ? "" : summary, plainText);

        StructuredChatCompletionCreateParams<QuizPack> params =
                StructuredChatCompletionCreateParams.<QuizPack>builder()
                        .model(ChatModel.GPT_4_1)
                        .temperature(0.2)
                        .maxCompletionTokens(600)
                        .responseFormat(QuizPack.class)
                        .addUserMessage(prompt)
                        .build();

        return openAIClient.chat()
                .completions()
                .create(params)
                .choices()
                .stream()
                .flatMap(c -> c.message().content().stream())
                .findFirst()
                .orElse(null);
    }
}



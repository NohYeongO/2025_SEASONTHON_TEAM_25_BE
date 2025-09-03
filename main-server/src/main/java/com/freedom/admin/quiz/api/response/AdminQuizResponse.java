package com.freedom.admin.quiz.api.response;

import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminQuizResponse {
    private Long id;
    private QuizType type;
    private String category;
    private String question;
    private Long newsArticleId;

    public static AdminQuizResponse from(Quiz q) {
        return AdminQuizResponse.builder()
                .id(q.getId())
                .type(q.getType())
                .category(q.getCategory())
                .question(q.getQuestion())
                .newsArticleId(q.getNewsArticleId())
                .build();
    }
}



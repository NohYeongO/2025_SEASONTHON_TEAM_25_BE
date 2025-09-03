package com.freedom.admin.quiz.api.response;

import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminQuizDetailResponse {
    private Long id;
    private QuizType type;
    private String category;
    private String question;
    private String explanation;
    private Boolean oxAnswer;
    private String mcqOption1;
    private String mcqOption2;
    private String mcqOption3;
    private String mcqOption4;
    private Integer mcqCorrectIndex;
    private Long newsArticleId;

    public static AdminQuizDetailResponse from(Quiz q) {
        return AdminQuizDetailResponse.builder()
                .id(q.getId())
                .type(q.getType())
                .category(q.getCategory())
                .question(q.getQuestion())
                .explanation(q.getExplanation())
                .oxAnswer(q.getOxAnswer())
                .mcqOption1(q.getMcqOption1())
                .mcqOption2(q.getMcqOption2())
                .mcqOption3(q.getMcqOption3())
                .mcqOption4(q.getMcqOption4())
                .mcqCorrectIndex(q.getMcqCorrectIndex())
                .newsArticleId(q.getNewsArticleId())
                .build();
    }
}



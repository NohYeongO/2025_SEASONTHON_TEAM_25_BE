package com.freedom.quiz.api.response;

import com.freedom.quiz.application.dto.DailyQuizDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyQuizResponse {
    private final boolean isCompleted;
    private final List<QuizResponse> quizzes;

    public static DailyQuizResponse from(DailyQuizDto dailyQuizDto) {
        List<QuizResponse> quizResponses = dailyQuizDto.getUserQuizzes().stream()
                .map(QuizResponse::from)
                .toList();

        return DailyQuizResponse.builder().isCompleted(dailyQuizDto.isCompleted()).quizzes(quizResponses).build();
    }
}

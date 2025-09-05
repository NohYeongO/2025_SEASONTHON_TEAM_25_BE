package com.freedom.quiz.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyQuizDto {
    private final boolean isCompleted;
    private final List<UserQuizDto> userQuizzes;

    public static DailyQuizDto from(boolean isCompleted, List<UserQuizDto> userQuizzes) {
        return DailyQuizDto.builder().isCompleted(isCompleted).userQuizzes(userQuizzes).build();
    }
}

package com.freedom.quiz.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizCategory {
    NEWS_ARTICLE("news"),
    ADMIN_CREATED("quiz");

    private final String displayName;
}

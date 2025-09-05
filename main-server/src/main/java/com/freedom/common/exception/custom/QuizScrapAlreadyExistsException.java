package com.freedom.common.exception.custom;

import lombok.Getter;

@Getter
public class QuizScrapAlreadyExistsException extends RuntimeException {
    
    private final Long userId;
    private final Long userQuizId;
    
    public QuizScrapAlreadyExistsException(Long userId, Long userQuizId) {
        super(String.format("이미 스크랩한 퀴즈입니다. userId: %d, userQuizId: %d", userId, userQuizId));
        this.userId = userId;
        this.userQuizId = userQuizId;
    }
    
    public QuizScrapAlreadyExistsException(String message, Long userId, Long userQuizId) {
        super(message);
        this.userId = userId;
        this.userQuizId = userQuizId;
    }
}

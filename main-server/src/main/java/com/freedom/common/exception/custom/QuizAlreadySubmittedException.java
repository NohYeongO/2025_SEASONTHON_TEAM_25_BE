package com.freedom.common.exception.custom;

/**
 * 이미 답안이 제출된 퀴즈에 다시 답안을 제출하려 할 때 발생하는 예외
 */
public class QuizAlreadySubmittedException extends RuntimeException {
    
    public QuizAlreadySubmittedException(String message) {
        super(message);
    }
    
    public QuizAlreadySubmittedException(Long userQuizId) {
        super("이미 답안이 제출된 퀴즈입니다. userQuizId=" + userQuizId);
    }
    
    public QuizAlreadySubmittedException(Long userId, Long quizId) {
        super("이미 답안이 제출된 퀴즈입니다. userId=" + userId + ", quizId=" + quizId);
    }
}

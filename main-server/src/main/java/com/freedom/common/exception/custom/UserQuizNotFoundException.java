package com.freedom.common.exception.custom;

/**
 * 사용자 퀴즈를 찾을 수 없을 때 발생하는 예외
 */
public class UserQuizNotFoundException extends RuntimeException {
    
    public UserQuizNotFoundException(String message) {
        super(message);
    }
    
    public UserQuizNotFoundException(Long userQuizId) {
        super("사용자 퀴즈를 찾을 수 없습니다. userQuizId=" + userQuizId);
    }
    
    public UserQuizNotFoundException(Long userId, Long quizId) {
        super("사용자 퀴즈를 찾을 수 없습니다. userId=" + userId + ", quizId=" + quizId);
    }
}

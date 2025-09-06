package com.freedom.common.exception.custom;

/**
 * 유효하지 않은 퀴즈 답안일 때 발생하는 예외
 */
public class InvalidQuizAnswerException extends RuntimeException {
    
    public InvalidQuizAnswerException(String message) {
        super(message);
    }
    
    public InvalidQuizAnswerException(String answer, String validOptions) {
        super("유효하지 않은 답안입니다. 입력된 답안: " + answer + ", 유효한 답안: " + validOptions);
    }
    
    public InvalidQuizAnswerException(String answer, int maxOption) {
        super("유효하지 않은 선택지입니다. 입력된 답안: " + answer + ", 유효 범위: 1-" + maxOption);
    }
}

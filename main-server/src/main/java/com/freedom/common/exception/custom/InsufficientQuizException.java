package com.freedom.common.exception.custom;

public class InsufficientQuizException extends RuntimeException {
    
    public InsufficientQuizException(String message) {
        super(message);
    }
    
    public InsufficientQuizException(int availableCount, int requiredCount) {
        super(String.format("퀴즈가 부족합니다. 필요: %d개, 사용 가능: %d개", requiredCount, availableCount));
    }
}

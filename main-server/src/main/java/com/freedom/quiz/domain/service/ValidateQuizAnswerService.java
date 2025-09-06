package com.freedom.quiz.domain.service;

import com.freedom.common.exception.custom.InvalidQuizAnswerException;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateQuizAnswerService {

    private final FindQuizService findQuizService;

    /**
     * 퀴즈 답안 검증 및 유효성 확인
     */
    public boolean validateAnswer(Long quizId, String userAnswer) {
        Quiz quiz = findQuizService.findById(quizId);
        
        // 답안 유효성 검증
        validateAnswerFormat(quiz, userAnswer);
        
        return quiz.isCorrectAnswer(userAnswer);
    }
    
    /**
     * 답안 형식 유효성 검증
     */
    private void validateAnswerFormat(Quiz quiz, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            throw new InvalidQuizAnswerException("답안이 비어있습니다.");
        }
        
        if (quiz.getType() == QuizType.MCQ) {
            validateMcqAnswer(userAnswer);
        } else if (quiz.getType() == QuizType.OX) {
            validateOxAnswer(userAnswer);
        }
    }
    
    /**
     * 객관식 답안 유효성 검증 (1~4 범위)
     */
    private void validateMcqAnswer(String userAnswer) {
        try {
            int answerIndex = Integer.parseInt(userAnswer);
            if (answerIndex < 1 || answerIndex > 4) {
                throw new InvalidQuizAnswerException(userAnswer, 4);
            }
        } catch (NumberFormatException e) {
            throw new InvalidQuizAnswerException("객관식 답안은 1~4 사이의 숫자여야 합니다. 입력된 값: " + userAnswer);
        }
    }
    
    /**
     * OX 답안 유효성 검증
     */
    private void validateOxAnswer(String userAnswer) {
        String normalizedAnswer = userAnswer.toLowerCase().trim();
        if (!"true".equals(normalizedAnswer) && !"false".equals(normalizedAnswer)) {
            throw new InvalidQuizAnswerException("OX 답안은 true 또는 false여야 합니다. 입력된 값: " + userAnswer);
        }
    }
}

package com.freedom.quiz.application.dto;

import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.QuizType;
import com.freedom.quiz.domain.entity.UserQuiz;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserQuizDto {
    private final Long userQuizId;
    private final Long quizId;
    private final QuizType type;
    private final String question;
    private final String explanation;
    
    // OX 타입
    private final Boolean oxAnswer;
    
    // MCQ 타입
    private final String mcqOption1;
    private final String mcqOption2;
    private final String mcqOption3;
    private final String mcqOption4;
    private final Integer mcqCorrectIndex;
    
    // 사용자 답안 정보
    private final String userAnswer;
    private final Boolean isCorrect;

    private final String newsUrl;

    public static UserQuizDto from(UserQuiz userQuiz, Quiz quiz, String newsUrl) {
        return UserQuizDto.builder()
                .userQuizId(userQuiz.getId())
                .quizId(quiz.getId())
                .type(quiz.getType())
                .question(quiz.getQuestion())
                .explanation(quiz.getExplanation())
                .oxAnswer(quiz.getOxAnswer())
                .mcqOption1(quiz.getMcqOption1())
                .mcqOption2(quiz.getMcqOption2())
                .mcqOption3(quiz.getMcqOption3())
                .mcqOption4(quiz.getMcqOption4())
                .mcqCorrectIndex(quiz.getMcqCorrectIndex())
                .userAnswer(userQuiz.getUserAnswer())
                .isCorrect(userQuiz.getIsCorrect())
                .newsUrl(newsUrl)
                .build();
    }
}

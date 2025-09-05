package com.freedom.quiz.api.response;

import com.freedom.quiz.application.dto.UserQuizDto;
import com.freedom.quiz.domain.entity.QuizType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuizResponse {
    private final Long userQuizId;
    private final Long quizId;
    private final QuizType type;
    private final String question;
    private final Boolean oxAnswer;  // OX 타입일 때만 사용
    private final List<String> mcqOptions;  // 객관식일 때만 사용
    private final Integer mcqCorrectIndex;  // 객관식 정답 인덱스
    private final String userAnswer;  // 사용자가 제출한 답안
    private final String explanation; // 해설
    private final Boolean isCorrect;  // 정답 여부 (null이면 미풀이)
    private final String newsUrl; // 관련 뉴스 URL

    public static QuizResponse from(UserQuizDto userQuizDto) {
        List<String> mcqOptions = null;
        if (userQuizDto.getType() == QuizType.MCQ) {
            mcqOptions = List.of(
                    userQuizDto.getMcqOption1(),
                    userQuizDto.getMcqOption2(),
                    userQuizDto.getMcqOption3(),
                    userQuizDto.getMcqOption4()
            );
        }

        return QuizResponse.builder().userQuizId(userQuizDto.getUserQuizId())
                .quizId(userQuizDto.getQuizId())
                .type(userQuizDto.getType())
                .question(userQuizDto.getQuestion())
                .oxAnswer(userQuizDto.getOxAnswer())
                .mcqOptions(mcqOptions)
                .mcqCorrectIndex(userQuizDto.getMcqCorrectIndex())
                .userAnswer(userQuizDto.getUserAnswer())
                .explanation(userQuizDto.getExplanation())
                .isCorrect(userQuizDto.getIsCorrect())
                .newsUrl(userQuizDto.getNewsUrl())
                .build();
    }
}

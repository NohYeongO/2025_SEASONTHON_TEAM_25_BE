package com.freedom.scrap.api.response;

import com.freedom.quiz.domain.entity.QuizDifficulty;
import com.freedom.quiz.domain.entity.QuizType;
import com.freedom.scrap.application.dto.QuizScrapDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizScrapListResponse {
    
    private Long userQuizId;
    private Long quizId;
    private String scrappedDate;  // YYYY.MM.DD 형태
    private String quizDate;      // YYYY.MM.DD 형태 (퀴즈 출제일)
    private QuizType quizType;    // OX, MCQ
    private QuizDifficulty difficulty;
    private String category;
    private String question;
    private String explanation;
    private Boolean isCorrectAtScrap; // 스크랩 당시 정답/오답 여부
    private String userAnswer;        // 사용자가 선택한 답
    private boolean isScraped;        // 스크랩 여부 (항상 true)
    
    // OX 퀴즈용 필드
    private Boolean oxAnswer;
    
    // MCQ 퀴즈용 필드
    private String mcqOption1;
    private String mcqOption2;
    private String mcqOption3;
    private String mcqOption4;
    private Integer mcqCorrectIndex;
    
    /**
     * QuizScrapDto를 Response로 변환
     */
    public static QuizScrapListResponse from(QuizScrapDto dto) {
        return QuizScrapListResponse.builder()
                .userQuizId(dto.getUserQuizId())
                .quizId(dto.getQuizId())
                .scrappedDate(dto.getScrappedDate())
                .quizDate(dto.getQuizDate())
                .quizType(dto.getQuizType())
                .difficulty(dto.getDifficulty())
                .category(dto.getCategory())
                .question(dto.getQuestion())
                .explanation(dto.getExplanation())
                .isCorrectAtScrap(dto.getIsCorrectAtScrap())
                .userAnswer(dto.getUserAnswer())
                .isScraped(dto.isScraped())
                .oxAnswer(dto.getOxAnswer())
                .mcqOption1(dto.getMcqOption1())
                .mcqOption2(dto.getMcqOption2())
                .mcqOption3(dto.getMcqOption3())
                .mcqOption4(dto.getMcqOption4())
                .mcqCorrectIndex(dto.getMcqCorrectIndex())
                .build();
    }
}

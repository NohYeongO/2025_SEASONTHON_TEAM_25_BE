package com.freedom.scrap.application.dto;

import com.freedom.quiz.domain.entity.QuizDifficulty;
import com.freedom.quiz.domain.entity.QuizType;
import com.freedom.scrap.domain.entity.QuizScrap;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class QuizScrapDto {
    
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
     * QuizScrap 엔티티를 DTO로 변환
     */
    public static QuizScrapDto from(QuizScrap quizScrap) {
        return QuizScrapDto.builder()
                .userQuizId(quizScrap.getUserQuiz().getId())
                .quizId(quizScrap.getUserQuiz().getQuiz().getId())
                .scrappedDate(formatDate(quizScrap.getScrappedDate()))
                .quizDate(formatDate(quizScrap.getUserQuiz().getQuizDate()))
                .quizType(quizScrap.getUserQuiz().getQuiz().getType())
                .difficulty(quizScrap.getUserQuiz().getQuiz().getDifficulty())
                .category(quizScrap.getUserQuiz().getQuiz().getCategory())
                .question(quizScrap.getUserQuiz().getQuiz().getQuestion())
                .explanation(quizScrap.getUserQuiz().getQuiz().getExplanation())
                .isCorrectAtScrap(quizScrap.getIsCorrectAtScrap())
                .userAnswer(quizScrap.getUserQuiz().getUserAnswer())
                .isScraped(true) // 스크랩 목록이므로 항상 true
                // OX 퀴즈 필드
                .oxAnswer(quizScrap.getUserQuiz().getQuiz().getOxAnswer())
                // MCQ 퀴즈 필드
                .mcqOption1(quizScrap.getUserQuiz().getQuiz().getMcqOption1())
                .mcqOption2(quizScrap.getUserQuiz().getQuiz().getMcqOption2())
                .mcqOption3(quizScrap.getUserQuiz().getQuiz().getMcqOption3())
                .mcqOption4(quizScrap.getUserQuiz().getQuiz().getMcqOption4())
                .mcqCorrectIndex(quizScrap.getUserQuiz().getQuiz().getMcqCorrectIndex())
                .build();
    }
    
    /**
     * 날짜를 YYYY.MM.DD 형태로 포맷
     */
    private static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}

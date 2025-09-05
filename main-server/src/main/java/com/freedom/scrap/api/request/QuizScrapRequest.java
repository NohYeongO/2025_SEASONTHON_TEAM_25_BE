package com.freedom.scrap.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class QuizScrapRequest {
    
    @NotNull(message = "UserQuiz ID는 필수입니다.")
    private Long userQuizId;
    
    @NotNull(message = "정답/오답 여부는 필수입니다.")
    private Boolean isCorrect;
}

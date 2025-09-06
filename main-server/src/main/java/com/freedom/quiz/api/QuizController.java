package com.freedom.quiz.api;

import com.freedom.common.logging.Loggable;
import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.quiz.api.request.QuizAnswerRequest;
import com.freedom.quiz.api.response.DailyQuizResponse;
import com.freedom.quiz.application.QuizFacade;
import com.freedom.quiz.application.dto.DailyQuizDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizFacade quizFacade;

    @GetMapping("/daily")
    @Loggable("데일리 퀴즈 조회")
    public ResponseEntity<DailyQuizResponse> getDailyQuizzes(@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        
        DailyQuizDto dailyQuizDto = quizFacade.getDailyQuizzes(userId);
        DailyQuizResponse response = DailyQuizResponse.from(dailyQuizDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/answer")
    @Loggable("퀴즈 답안 제출")
    public ResponseEntity<Void> submitAnswer(@RequestBody @Valid QuizAnswerRequest request) {
        
        quizFacade.submitQuizAnswer(request.getUserQuizId(), request.getUserAnswer());
        
        return ResponseEntity.ok().build();
    }
}

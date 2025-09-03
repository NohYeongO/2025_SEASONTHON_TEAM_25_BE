package com.freedom.admin.quiz.api;

import com.freedom.admin.quiz.api.response.AdminQuizDetailResponse;
import com.freedom.admin.quiz.api.response.AdminQuizResponse;
import com.freedom.admin.quiz.application.AdminQuizService;
import com.freedom.common.dto.PageResponse;
import com.freedom.common.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/quiz")
@RequiredArgsConstructor
public class AdminQuizController {

    private final AdminQuizService adminQuizService;

    @GetMapping
    @Loggable("관리자 퀴즈 목록 조회")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminQuizResponse>> getQuizList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PageResponse<AdminQuizResponse> response = adminQuizService.getQuizList(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}")
    @Loggable("관리자 퀴즈 상세 조회")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminQuizDetailResponse> getQuizDetail(@PathVariable Long quizId) {
        AdminQuizDetailResponse response = adminQuizService.getQuizDetail(quizId);
        return ResponseEntity.ok(response);
    }
}



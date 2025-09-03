package com.freedom.admin.quiz.application;

import com.freedom.admin.quiz.api.response.AdminQuizDetailResponse;
import com.freedom.admin.quiz.api.response.AdminQuizResponse;
import com.freedom.common.dto.PageResponse;
import com.freedom.common.logging.Loggable;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.infra.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQuizService {

    private final QuizRepository quizRepository;

    @Loggable("관리자 퀴즈 목록 조회")
    public PageResponse<AdminQuizResponse> getQuizList(Pageable pageable) {
        Page<Quiz> page = quizRepository.findAll(pageable);
        Page<AdminQuizResponse> mapped = page.map(AdminQuizResponse::from);
        return PageResponse.of(mapped);
    }

    @Loggable("관리자 퀴즈 상세 조회")
    public AdminQuizDetailResponse getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("퀴즈를 찾을 수 없습니다. id=" + quizId));
        return AdminQuizDetailResponse.from(quiz);
    }
}



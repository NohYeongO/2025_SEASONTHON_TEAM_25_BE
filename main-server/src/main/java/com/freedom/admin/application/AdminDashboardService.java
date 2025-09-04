package com.freedom.admin.application;

import com.freedom.admin.api.dto.DashboardStatsResponse;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.common.logging.Loggable;
import com.freedom.news.infra.repository.NewsArticleRepository;
import com.freedom.quiz.infra.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final NewsArticleRepository newsArticleRepository;
    private final QuizRepository quizRepository;
    private final UserJpaRepository userRepository;

    @Loggable("대시보드 통계 조회")
    public DashboardStatsResponse getDashboardStats() {
        long totalNewsCount = newsArticleRepository.count();
        long totalQuizCount = quizRepository.count();
        long totalUserCount = userRepository.countByRole(UserRole.USER);
        
        return DashboardStatsResponse.of(totalNewsCount, totalQuizCount, totalUserCount);
    }
}

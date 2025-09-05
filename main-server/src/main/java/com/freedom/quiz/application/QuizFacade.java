package com.freedom.quiz.application;

import com.freedom.common.exception.custom.UserQuizNotFoundException;
import com.freedom.news.domain.service.FindNewsService;
import com.freedom.quiz.application.dto.DailyQuizDto;
import com.freedom.quiz.application.dto.UserQuizDto;
import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.UserQuiz;
import com.freedom.quiz.domain.service.CreateDailyQuizService;
import com.freedom.quiz.domain.service.FindQuizService;
import com.freedom.quiz.domain.service.FindUserQuizService;
import com.freedom.quiz.domain.service.UpdateUserQuizService;
import com.freedom.quiz.domain.service.ValidateQuizAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizFacade {

    private final FindUserQuizService findUserQuizService;
    private final CreateDailyQuizService createDailyQuizService;
    private final FindQuizService findQuizService;
    private final UpdateUserQuizService updateUserQuizService;
    private final ValidateQuizAnswerService validateQuizAnswerService;
    private final FindNewsService findNewsService;
    /**
     * 일일 퀴즈 조회 (없으면 생성)
     */
    @Transactional
    public DailyQuizDto getDailyQuizzes(Long userId) {
        LocalDate today = LocalDate.now();
        boolean isCompleted = false;
        // 오늘의 퀴즈 조회
        List<UserQuiz> userQuizzes = findUserQuizService.findDailyQuizzes(userId, today);
        
        // 퀴즈가 없으면 새로 생성
        if (userQuizzes.isEmpty()) {
            userQuizzes = createDailyQuizService.createDailyQuizzes(userId, today);
        } else {
            long correctCount = userQuizzes.stream()
                    .mapToLong(uq -> Boolean.TRUE.equals(uq.getIsCorrect()) ? 1 : 0)
                    .sum();
            int totalQuizCount = userQuizzes.size();
            isCompleted = correctCount == totalQuizCount;
        }
        // 미풀이 + 오답 문제만 필터링
        List<UserQuiz> activeQuizzes = userQuizzes.stream()
                .filter(uq -> uq.getIsCorrect() == null || !uq.getIsCorrect())
                .toList();

        // 퀴즈 정보 조회
        List<Long> quizIds = activeQuizzes.stream()
                .map(q -> q.getQuiz().getId())
                .toList();
        
        Map<Long, Quiz> quizMap = findQuizService.findQuizzesByIds(quizIds);

        // DTO 변환 시 null 체크
        List<UserQuizDto> userQuizDtos = activeQuizzes.stream()
                .map(uq -> {
                    Quiz quiz = quizMap.get(uq.getQuiz().getId());
                    if (quiz == null) {
                        log.error("퀴즈를 찾을 수 없습니다. quizId: {}", uq.getQuiz().getId());
                        return null;
                    }
                    String newsOriginalUrl = quiz.getNewsArticleId() != null ? findNewsService.findNewsById(quiz.getNewsArticleId()).getOriginalUrl() : null;

                    return UserQuizDto.from(uq, quiz, newsOriginalUrl);
                })
                .filter(Objects::nonNull)
                .toList();

        return DailyQuizDto.from(isCompleted, userQuizDtos);
    }

    /**
     * 퀴즈 답안 제출 - 예외 처리 강화
     */
    @Transactional
    public void submitQuizAnswer(Long userQuizId, String userAnswer) {
        // UserQuiz 존재 여부 사전 확인
        if (!findUserQuizService.existsUserQuiz(userQuizId)) {
            log.warn("존재하지 않는 UserQuiz 접근 시도. userQuizId: {}", userQuizId);
            throw new UserQuizNotFoundException(userQuizId);
        }

        // 퀴즈 ID 조회 (내부적으로 예외 처리됨)
        Long quizId = findUserQuizService.findQuizIdByUserQuizId(userQuizId);
        
        // 정답 검증
        boolean isCorrect = validateQuizAnswerService.validateAnswer(quizId, userAnswer);
        
        // 답안 업데이트
        updateUserQuizService.updateAnswer(userQuizId, userAnswer, isCorrect);
    }
}

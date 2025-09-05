package com.freedom.quiz.domain.service;

import com.freedom.quiz.domain.entity.Quiz;
import com.freedom.quiz.domain.entity.UserQuiz;
import com.freedom.common.exception.custom.InsufficientQuizException;
import com.freedom.quiz.infra.QuizRepository;
import com.freedom.quiz.infra.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDailyQuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    
    private static final int DAILY_QUIZ_COUNT = 5;
    private static final Long EMPTY_LIST_DUMMY_ID = -1L;

    /**
     * 특정 사용자를 위한 일일 퀴즈 생성
     */
    public List<UserQuiz> createDailyQuizzes(Long userId, LocalDate quizDate) {
        List<Long> excludeQuizIds = getExcludeQuizIds(userId, quizDate);
        List<Quiz> selectedQuizzes = selectQuizzesByDay(quizDate, excludeQuizIds);

        // 퀴즈 부족 시 예외 처리
        if (selectedQuizzes.isEmpty()) {
            throw new InsufficientQuizException("출제 가능한 퀴즈가 없습니다.");
        }

        // 5개보다 많으면 랜덤으로 5개 선택
        if (selectedQuizzes.size() > DAILY_QUIZ_COUNT) {
            Collections.shuffle(selectedQuizzes);
            selectedQuizzes = selectedQuizzes.subList(0, DAILY_QUIZ_COUNT);
        }

        // 5개보다 적으면 경고 로그
        if (selectedQuizzes.size() < DAILY_QUIZ_COUNT) {
            log.warn("일일 퀴즈가 {}개 부족합니다. 사용 가능: {}개, 필요: {}개", 
                    DAILY_QUIZ_COUNT - selectedQuizzes.size(), 
                    selectedQuizzes.size(), 
                    DAILY_QUIZ_COUNT);
            throw new InsufficientQuizException("출제 가능한 퀴즈가 부족합니다. 현재 " + selectedQuizzes.size() + "개, 필요 " + DAILY_QUIZ_COUNT + "개");
        }

        // UserQuiz 엔티티로 변환 및 저장
        List<UserQuiz> userQuizzes = selectedQuizzes.stream()
                .map(quiz -> UserQuiz.builder()
                        .userId(userId)
                        .quiz(Quiz.builder().id(quiz.getId()).build())
                        .quizDate(quizDate)
                        .assignedDate(quizDate) // assignedDate 추가
                        .build())
                .toList();

        return userQuizRepository.saveAll(userQuizzes);
    }

    private List<Long> getExcludeQuizIds(Long userId, LocalDate quizDate) {
        List<Long> todayQuizIds = userQuizRepository.findQuizIdsByUserIdAndQuizDate(userId, quizDate);
        
        if (isWeekend(quizDate)) {
            // 주말: 정답 맞춘 퀴즈 + 오늘 이미 출제된 퀴즈 제외
            List<Long> correctQuizIds = userQuizRepository.findCorrectQuizIdsByUserId(userId);
            return mergeAndDistinct(todayQuizIds, correctQuizIds);
        } else {
            // 주중: 오늘 이미 출제된 퀴즈만 제외
            return todayQuizIds;
        }
    }

    private List<Quiz> selectQuizzesByDay(LocalDate quizDate, List<Long> excludeQuizIds) {
        // 빈 리스트일 때 더미 값 추가 (SQL IN 절 오류 방지)
        if (excludeQuizIds.isEmpty()) {
            excludeQuizIds = List.of(EMPTY_LIST_DUMMY_ID);
        }

        if (isWeekend(quizDate)) {
            return quizRepository.findWeekendQuizzes(excludeQuizIds);
        } else {
            LocalDateTime startDate = quizDate.minusDays(1).atStartOfDay();
            LocalDateTime endDate = quizDate.plusDays(1).atStartOfDay();   // 내일 00:00
            return quizRepository.findWeekdayQuizzes(startDate, endDate, excludeQuizIds);
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private List<Long> mergeAndDistinct(List<Long> list1, List<Long> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .distinct()
                .toList();
    }
}

package com.freedom.quiz.domain.service;

import com.freedom.common.exception.custom.UserQuizNotFoundException;
import com.freedom.quiz.domain.entity.UserQuiz;
import com.freedom.quiz.infra.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindUserQuizService {

    private final UserQuizRepository userQuizRepository;

    /**
     * 특정 사용자의 특정 날짜 퀴즈 조회
     */
    public List<UserQuiz> findDailyQuizzes(Long userId, LocalDate quizDate) {
        return userQuizRepository.findByUserIdAndQuizDate(userId, quizDate);
    }

    /**
     * 특정 사용자가 정답 맞춘 모든 퀴즈 ID 조회 (주말용)
     */
    public List<Long> findCorrectQuizIds(Long userId) {
        return userQuizRepository.findCorrectQuizIdsByUserId(userId);
    }

    /**
     * 특정 사용자의 특정 날짜에 이미 출제된 퀴즈 ID 조회
     */
    public List<Long> findTodayQuizIds(Long userId, LocalDate quizDate) {
        return userQuizRepository.findQuizIdsByUserIdAndQuizDate(userId, quizDate);
    }

    /**
     * UserQuiz ID로 퀴즈 ID 조회 - 예외 처리 강화
     */
    public Long findQuizIdByUserQuizId(Long userQuizId) {
        Long quizId = userQuizRepository.findQuizIdByUserQuizId(userQuizId);
        if (quizId == null) {
            throw new UserQuizNotFoundException(userQuizId);
        }
        return quizId;
    }

    /**
     * UserQuiz 존재 여부 확인
     */
    public boolean existsUserQuiz(Long userQuizId) {
        return userQuizRepository.existsById(userQuizId);
    }
}

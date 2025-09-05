package com.freedom.quiz.infra;

import com.freedom.quiz.domain.entity.UserQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {

    /**
     * 특정 사용자의 특정 날짜 퀴즈 조회
     */
    List<UserQuiz> findByUserIdAndQuizDate(Long userId, LocalDate quizDate);

    /**
     * 특정 사용자가 정답 맞춘 모든 퀴즈 ID 조회 (주말용 - 중복 방지)
     */
    @Query("SELECT uq.quiz.id FROM UserQuiz uq WHERE uq.userId = :userId AND uq.isCorrect = true")
    List<Long> findCorrectQuizIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 특정 날짜에 이미 출제된 퀴즈 ID 조회 (중복 방지)
     */
    @Query("SELECT uq.quiz.id FROM UserQuiz uq WHERE uq.userId = :userId AND uq.quizDate = :quizDate")
    List<Long> findQuizIdsByUserIdAndQuizDate(@Param("userId") Long userId, @Param("quizDate") LocalDate quizDate);

    /**
     * UserQuiz ID로 퀴즈 ID 조회
     */
    @Query("SELECT uq.quiz.id FROM UserQuiz uq WHERE uq.id = :userQuizId")
    Long findQuizIdByUserQuizId(@Param("userQuizId") Long userQuizId);
}

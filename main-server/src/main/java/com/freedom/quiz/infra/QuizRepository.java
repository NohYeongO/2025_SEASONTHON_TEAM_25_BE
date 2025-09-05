package com.freedom.quiz.infra;

import com.freedom.quiz.domain.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q WHERE q.category = 'news' " +
           "AND q.createdAt >= :startDate AND q.createdAt < :endDate " +
           "AND q.id NOT IN :excludeQuizIds " +
           "ORDER BY FUNCTION('RAND')")
    List<Quiz> findWeekdayQuizzes(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  @Param("excludeQuizIds") List<Long> excludeQuizIds);

    @Query("SELECT q FROM Quiz q WHERE q.category = 'quiz' " +
           "AND q.id NOT IN :excludeQuizIds " +
           "ORDER BY FUNCTION('RAND')")
    List<Quiz> findWeekendQuizzes(@Param("excludeQuizIds") List<Long> excludeQuizIds);
}

package com.freedom.scrap.infra;

import com.freedom.scrap.domain.entity.QuizScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuizScrapRepository extends JpaRepository<QuizScrap, Long> {
    @Query("""
        SELECT qs FROM QuizScrap qs 
        JOIN FETCH qs.userQuiz uq
        JOIN FETCH uq.quiz q
        WHERE qs.user.id = :userId 
        ORDER BY qs.scrappedDate DESC, qs.createdAt DESC
        """)
    Page<QuizScrap> findByUserIdOrderByScrappedDateDesc(
        @Param("userId") Long userId, 
        Pageable pageable
    );
    
    /**
     * 사용자와 UserQuiz로 스크랩 존재 여부 확인
     */
    boolean existsByUserIdAndUserQuizId(Long userId, Long userQuizId);
    
    /**
     * 사용자별 퀴즈 스크랩 개수 조회
     */
    long countByUserId(Long userId);
}

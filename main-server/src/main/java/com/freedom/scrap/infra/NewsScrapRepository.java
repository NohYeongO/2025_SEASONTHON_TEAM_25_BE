package com.freedom.scrap.infra;

import com.freedom.scrap.domain.entity.NewsScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewsScrapRepository extends JpaRepository<NewsScrap, Long> {
    
    @Query("""
        SELECT ns FROM NewsScrap ns 
        WHERE ns.user.id = :userId 
        AND ns.newsArticle.id = :newsArticleId
        """)
    Optional<NewsScrap> findByUserIdAndNewsArticleId(
        @Param("userId") Long userId, 
        @Param("newsArticleId") Long newsArticleId
    );
    
    @Query("""
        SELECT ns FROM NewsScrap ns 
        JOIN FETCH ns.newsArticle na
        WHERE ns.user.id = :userId 
        ORDER BY ns.scrappedDate DESC, ns.createdAt DESC
        """)
    Page<NewsScrap> findByUserIdOrderByScrappedDateDesc(
        @Param("userId") Long userId, 
        Pageable pageable
    );
    
    /**
     * 사용자와 뉴스 기사로 스크랩 존재 여부 확인
     */
    boolean existsByUserIdAndNewsArticleId(Long userId, Long newsArticleId);
    
    /**
     * 사용자별 스크랩 개수 조회
     */
    long countByUserId(Long userId);
}

package com.freedom.news.infra.repository;

import com.freedom.admin.news.application.dto.ExistingNewsDto;
import com.freedom.news.domain.entity.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    @Query("""
        SELECT new com.freedom.admin.news.application.dto.ExistingNewsDto(
            n.newsItemId, n.modifyId, n.contentHash
        )
        FROM NewsArticle n
        WHERE n.newsItemId IN :newsItemIds
          AND n.modifyDate BETWEEN :startOfDay AND :endOfDay
    """)
    List<ExistingNewsDto> findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            @Param("newsItemIds") List<String> newsItemIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
        SELECT new com.freedom.admin.news.application.dto.ExistingNewsDto(
            n.newsItemId, n.modifyId, n.contentHash
        )
        FROM NewsArticle n
        WHERE n.newsItemId IN :newsItemIds
    """)
    List<ExistingNewsDto> findNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            @Param("newsItemIds") List<String> newsItemIds
    );


    Optional<NewsArticle> findByNewsItemId(String newsItemId);
    
    @Query("SELECT na FROM NewsArticle na WHERE na.approveDate >= :startDate AND na.approveDate < :endDate ORDER BY na.approveDate DESC")
    Page<NewsArticle> findRecentNewsByApproveDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}

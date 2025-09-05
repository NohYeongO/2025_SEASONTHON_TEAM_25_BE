package com.freedom.scrap.domain.entity;

import com.freedom.auth.domain.User;
import com.freedom.common.entity.BaseEntity;
import com.freedom.news.domain.entity.NewsArticle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "news_scrap",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_news_article",
        columnNames = {"user_id", "news_article_id"}
    ),
    indexes = {
        @Index(name = "idx_user_id_scrapped_date", columnList = "user_id, scrapped_date DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsScrap extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_article_id", nullable = false)
    private NewsArticle newsArticle;
    
    @Column(name = "scrapped_date", nullable = false)
    private LocalDate scrappedDate;
    
    @Builder
    public NewsScrap(User user, NewsArticle newsArticle, LocalDate scrappedDate) {
        this.user = user;
        this.newsArticle = newsArticle;
        this.scrappedDate = scrappedDate != null ? scrappedDate : LocalDate.now();
    }
    
    /**
     * 스크랩 생성을 위한 팩토리 메서드
     */
    public static NewsScrap create(User user, NewsArticle newsArticle) {
        return NewsScrap.builder()
                .user(user)
                .newsArticle(newsArticle)
                .scrappedDate(LocalDate.now())
                .build();
    }
    
    /**
     * 동일한 사용자와 뉴스 기사인지 확인
     */
    public boolean isSameUserAndArticle(Long userId, Long newsArticleId) {
        return this.user.getId().equals(userId) && 
               this.newsArticle.getId().equals(newsArticleId);
    }
}

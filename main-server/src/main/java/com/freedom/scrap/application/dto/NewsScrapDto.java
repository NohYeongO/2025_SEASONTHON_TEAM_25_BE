package com.freedom.scrap.application.dto;

import com.freedom.scrap.domain.entity.NewsScrap;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class NewsScrapDto {
    
    private Long newsArticleId;
    private String scrappedDate;  // YYYY.MM.DD 형태
    private String title;
    private String aiSummary;
    private String thumbnailUrl;
    private String approveDate;   // YYYY/MM/DD HH:mm 형태
    private boolean isScraped;
    
    /**
     * NewsScrap 엔티티를 DTO로 변환
     */
    public static NewsScrapDto from(NewsScrap newsScrap) {
        return NewsScrapDto.builder()
                .newsArticleId(newsScrap.getNewsArticle().getId())
                .scrappedDate(formatScrappedDate(newsScrap.getScrappedDate()))
                .title(newsScrap.getNewsArticle().getTitle())
                .aiSummary(newsScrap.getNewsArticle().getAiSummary())
                .thumbnailUrl(newsScrap.getNewsArticle().getThumbnailUrl())
                .approveDate(formatApproveDate(newsScrap.getNewsArticle().getApproveDate()))
                .isScraped(true) // 스크랩 목록이므로 항상 true
                .build();
    }
    
    /**
     * 스크랩한 날짜를 YYYY.MM.DD 형태로 포맷
     */
    private static String formatScrappedDate(LocalDate scrappedDate) {
        if (scrappedDate == null) {
            return null;
        }
        return scrappedDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
    
    /**
     * 승인 날짜를 YYYY/MM/DD HH:mm 형태로 포맷
     */
    private static String formatApproveDate(LocalDateTime approveDate) {
        if (approveDate == null) {
            return null;
        }
        return approveDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }
}

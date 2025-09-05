package com.freedom.scrap.api.response;

import com.freedom.scrap.application.dto.NewsScrapDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsScrapListResponse {
    
    private Long newsArticleId;
    private String scrappedDate;  // YYYY.MM.DD 형태
    private String title;
    private String aiSummary;
    private String thumbnailUrl;
    private String approveDate;   // YYYY/MM/DD HH:mm 형태
    private boolean isScraped;
    
    /**
     * NewsScrapDto를 Response로 변환
     */
    public static NewsScrapListResponse from(NewsScrapDto dto) {
        return NewsScrapListResponse.builder()
                .newsArticleId(dto.getNewsArticleId())
                .scrappedDate(dto.getScrappedDate())
                .title(dto.getTitle())
                .aiSummary(dto.getAiSummary())
                .thumbnailUrl(dto.getThumbnailUrl())
                .approveDate(dto.getApproveDate())
                .isScraped(dto.isScraped())
                .build();
    }
}

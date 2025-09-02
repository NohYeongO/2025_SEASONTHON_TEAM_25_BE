package com.freedom.news.api.response;

import com.freedom.news.application.dto.NewsDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NewsDetailResponse {
    
    private Long id;
    private String newsItemId;
    private String title;
    private LocalDateTime approveDate;
    private LocalDateTime modifyDate;
    private String thumbnailUrl;
    private String aiSummary;
    private String plainTextContent;
    private String ministerCode;
    private List<NewsContentBlockResponse> contentBlocks;
    
    public static NewsDetailResponse from(NewsDetailDto newsDetailDto) {
        return NewsDetailResponse.builder()
                .id(newsDetailDto.getId())
                .newsItemId(newsDetailDto.getNewsItemId())
                .title(newsDetailDto.getTitle())
                .approveDate(newsDetailDto.getApproveDate())
                .modifyDate(newsDetailDto.getModifyDate())
                .thumbnailUrl(newsDetailDto.getThumbnailUrl())
                .aiSummary(newsDetailDto.getAiSummary())
                .plainTextContent(newsDetailDto.getPlainTextContent())
                .ministerCode(newsDetailDto.getMinisterCode())
                .contentBlocks(newsDetailDto.getContentBlocks()
                        .stream()
                        .map(NewsContentBlockResponse::from)
                        .toList())
                .build();
    }
}

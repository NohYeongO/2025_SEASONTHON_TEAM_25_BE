package com.freedom.news.api.response;

import com.freedom.news.application.dto.NewsContentBlockDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsContentBlockResponse {
    
    private String blockType;
    private String originalContent;
    private String plainContent;
    private String url;
    private String altText;
    private Integer blockOrder;
    
    public static NewsContentBlockResponse from(NewsContentBlockDto dto) {
        return NewsContentBlockResponse.builder()
                .blockType(dto.getBlockType())
                .originalContent(dto.getOriginalContent())
                .plainContent(dto.getPlainContent())
                .url(dto.getUrl())
                .altText(dto.getAltText())
                .blockOrder(dto.getBlockOrder())
                .build();
    }
}

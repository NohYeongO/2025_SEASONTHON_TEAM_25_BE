package com.freedom.news.application.dto;

import com.freedom.news.domain.entity.NewsContentBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsContentBlockDto {
    
    private String blockType;
    private String originalContent;
    private String plainContent;
    private String url;
    private String altText;
    private Integer blockOrder;
    
    public static NewsContentBlockDto from(NewsContentBlock contentBlock) {
        return NewsContentBlockDto.builder()
                .blockType(contentBlock.getBlockType())
                .originalContent(contentBlock.getOriginalContent())
                .plainContent(contentBlock.getPlainContent())
                .url(contentBlock.getUrl())
                .altText(contentBlock.getAltText())
                .blockOrder(contentBlock.getBlockOrder())
                .build();
    }
}

package com.freedom.common.exception.custom;

import lombok.Getter;

@Getter
public class NewsScrapAlreadyExistsException extends RuntimeException {
    
    private final Long userId;
    private final Long newsArticleId;
    
    public NewsScrapAlreadyExistsException(Long userId, Long newsArticleId) {
        super(String.format("이미 스크랩한 뉴스입니다. userId: %d, newsArticleId: %d", userId, newsArticleId));
        this.userId = userId;
        this.newsArticleId = newsArticleId;
    }
    
    public NewsScrapAlreadyExistsException(String message, Long userId, Long newsArticleId) {
        super(message);
        this.userId = userId;
        this.newsArticleId = newsArticleId;
    }
}

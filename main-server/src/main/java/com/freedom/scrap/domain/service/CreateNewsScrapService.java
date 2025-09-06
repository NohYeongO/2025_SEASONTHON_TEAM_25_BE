package com.freedom.scrap.domain.service;

import com.freedom.auth.domain.User;
import com.freedom.common.exception.custom.NewsScrapAlreadyExistsException;
import com.freedom.common.logging.Loggable;
import com.freedom.news.domain.entity.NewsArticle;
import com.freedom.scrap.domain.entity.NewsScrap;
import com.freedom.scrap.infra.NewsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateNewsScrapService {
    
    private final NewsScrapRepository newsScrapRepository;
    
    /**
     * 뉴스 스크랩 생성
     * 
     * @param user 사용자
     * @param newsArticle 뉴스 기사
     * @return 생성된 뉴스 스크랩
     * @throws NewsScrapAlreadyExistsException 이미 스크랩한 뉴스인 경우
     */
    @Loggable("뉴스 스크랩 생성")
    public NewsScrap createNewsScrap(User user, NewsArticle newsArticle) {
        // 중복 스크랩 확인
        validateNotAlreadyScraped(user.getId(), newsArticle.getId());
        
        // 스크랩 생성
        NewsScrap newsScrap = NewsScrap.create(user, newsArticle);
        
        // 저장
        return newsScrapRepository.save(newsScrap);
    }
    
    /**
     * 이미 스크랩한 뉴스인지 검증
     */
    private void validateNotAlreadyScraped(Long userId, Long newsArticleId) {
        if (newsScrapRepository.existsByUserIdAndNewsArticleId(userId, newsArticleId)) {
            throw new NewsScrapAlreadyExistsException(userId, newsArticleId);
        }
    }
}

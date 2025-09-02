package com.freedom.news.domain.service;

import com.freedom.common.exception.custom.NewsNotFoundException;
import com.freedom.news.application.dto.NewsDetailDto;
import com.freedom.news.application.dto.NewsDto;
import com.freedom.news.domain.entity.NewsArticle;
import com.freedom.news.infra.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FindNewsService {
    
    private final NewsArticleRepository newsArticleRepository;

    @Transactional(readOnly = true)
    public Page<NewsDto> findRecentNews(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        // 토요일(6) 또는 일요일(7)인 경우
        if (today.getDayOfWeek().getValue() >= 6) {
            LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate friday = today.with(java.time.DayOfWeek.FRIDAY);
            startDate = monday.atStartOfDay();
            endDate   = friday.plusDays(1).atStartOfDay();
        } else {
            // 평일: 어제 00:00 ~ 내일 00:00  ➜ 어제+오늘 포함
            startDate = today.minusDays(1).atStartOfDay();
            endDate   = today.plusDays(1).atStartOfDay();
        }
        
        Page<NewsArticle> newsArticles = newsArticleRepository.findRecentNewsByApproveDateBetween(
            startDate, endDate, pageable
        );
        
        return newsArticles.map(NewsDto::from);
    }

    @Transactional(readOnly = true)
    public NewsDetailDto findNewsById(Long newsId) {
        NewsArticle newsArticle = newsArticleRepository.findById(newsId).orElseThrow(() -> new NewsNotFoundException("존재하지 않는 뉴스 입니다." + newsId));
        return NewsDetailDto.from(newsArticle, newsArticle.getContentBlocks());
    }
}

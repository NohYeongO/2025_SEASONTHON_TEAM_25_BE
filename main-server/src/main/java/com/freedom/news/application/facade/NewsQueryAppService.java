package com.freedom.news.application.facade;

import com.freedom.news.api.response.NewsDetailResponse;
import com.freedom.news.api.response.NewsResponse;
import com.freedom.news.application.dto.NewsDetailDto;
import com.freedom.news.application.dto.NewsDto;
import com.freedom.news.domain.service.FindNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsQueryAppService {
    
    private final FindNewsService findNewsService;
    
    public Page<NewsResponse> getRecentNewsList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsDto> newsDtos = findNewsService.findRecentNews(pageable);
        
        return newsDtos.map(NewsResponse::from);
    }

    public NewsDetailResponse getNewsDetail(Long newsId) {
        NewsDetailDto newsDetailDto = findNewsService.findNewsById(newsId);
        
        return NewsDetailResponse.from(newsDetailDto);
    }
}

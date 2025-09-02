package com.freedom.news.api;

import com.freedom.news.api.response.NewsDetailResponse;
import com.freedom.news.api.response.NewsResponse;
import com.freedom.news.application.facade.NewsQueryAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsQueryAppService newsQueryFacade;

    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNewsList(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<NewsResponse> newsList = newsQueryFacade.getRecentNewsList(page, size);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(@PathVariable Long newsId) {
        NewsDetailResponse newsDetail = newsQueryFacade.getNewsDetail(newsId);
        return ResponseEntity.ok(newsDetail);
    }

    @PostMapping("/{newsId}/scrap")
    public ResponseEntity<Void> scrapNews(@PathVariable Long newsId) {
        // TODO: newsService.scrapNews(newsId) 구현 필요

        return ResponseEntity.ok().build();
    }
}

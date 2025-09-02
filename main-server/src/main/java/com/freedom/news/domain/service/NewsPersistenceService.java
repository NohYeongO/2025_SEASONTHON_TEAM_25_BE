package com.freedom.news.domain.service;


import com.freedom.news.application.dto.NewsArticleDto;
import com.freedom.news.domain.entity.NewsArticle;
import com.freedom.news.infra.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsPersistenceService {
    private final NewsArticleRepository newsArticleRepository;

    public void saveNewArticles(List<NewsArticleDto> newArticles) {
        List<NewsArticle> entities = newArticles.stream()
                .map(NewsArticleDto::toEntity)
                .toList();
        newsArticleRepository.saveAll(entities);
    }

    public void updateArticles(List<NewsArticleDto> updatedArticles) {
        updatedArticles.forEach(dto -> {
            NewsArticle entity = newsArticleRepository.findByNewsItemId(dto.getNewsItemId())
                    .orElseThrow(() -> new IllegalStateException("기존 뉴스가 존재하지 않습니다. id=" + dto.getNewsItemId()));

            entity.updateFromDto(dto);
            newsArticleRepository.save(entity);
        });
    }
}

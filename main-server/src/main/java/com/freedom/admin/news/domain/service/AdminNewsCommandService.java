package com.freedom.admin.news.domain.service;

import com.freedom.admin.news.infra.repository.AdminNewsRepository;
import com.freedom.news.domain.entity.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNewsCommandService {

    private final AdminNewsRepository adminNewsRepository;

    public void deleteNews(Long newsId) {
        NewsArticle newsArticle = adminNewsRepository.findById(newsId)
            .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));
        
        adminNewsRepository.delete(newsArticle);
    }
}

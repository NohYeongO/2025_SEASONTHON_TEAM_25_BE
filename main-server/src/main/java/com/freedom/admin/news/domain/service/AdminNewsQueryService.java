package com.freedom.admin.news.domain.service;

import com.freedom.admin.news.application.dto.AdminNewsDto;
import com.freedom.admin.news.application.dto.AdminNewsDetailDto;
import com.freedom.admin.news.infra.repository.AdminNewsRepository;
import com.freedom.news.domain.entity.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNewsQueryService {

    private final AdminNewsRepository adminNewsRepository;

    public Page<AdminNewsDto> getNewsList(Pageable pageable) {
        Page<NewsArticle> newsPage = adminNewsRepository.findAllByOrderByApproveDateDesc(pageable);
        return newsPage.map(AdminNewsDto::from);
    }

    public AdminNewsDetailDto getNewsDetail(Long newsId) {
        NewsArticle newsArticle = adminNewsRepository.findByIdWithContentBlocks(newsId)
            .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));
        
        return AdminNewsDetailDto.from(newsArticle);
    }
}

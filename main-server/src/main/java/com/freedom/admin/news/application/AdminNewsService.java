package com.freedom.admin.news.application;

import com.freedom.admin.news.api.response.AdminNewsDetailResponse;
import com.freedom.admin.news.api.response.AdminNewsResponse;
import com.freedom.admin.news.application.dto.AdminNewsDto;
import com.freedom.admin.news.application.dto.AdminNewsDetailDto;
import com.freedom.admin.news.domain.service.AdminNewsQueryService;
import com.freedom.admin.news.domain.service.AdminNewsCommandService;
import com.freedom.admin.news.domain.service.NewsSyncService;
import com.freedom.common.dto.PageResponse;
import com.freedom.common.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNewsService {

    private final AdminNewsQueryService adminNewsQueryService;
    private final AdminNewsCommandService adminNewsCommandService;
    private final NewsSyncService newsSyncService;

    @Loggable("관리자 뉴스 목록 조회")
    public PageResponse<AdminNewsResponse> getNewsList(Pageable pageable) {
        Page<AdminNewsDto> newsPage = adminNewsQueryService.getNewsList(pageable);
        
        Page<AdminNewsResponse> responsePage = newsPage.map(AdminNewsResponse::from);
        
        return PageResponse.of(responsePage);
    }

    @Loggable("관리자 뉴스 상세 조회")
    public AdminNewsDetailResponse getNewsDetail(Long newsId) {
        AdminNewsDetailDto dto = adminNewsQueryService.getNewsDetail(newsId);
        return AdminNewsDetailResponse.from(dto);
    }

    @Transactional
    @Loggable("관리자 뉴스 삭제")
    public void deleteNews(Long newsId) {
        adminNewsCommandService.deleteNews(newsId);
    }

    @Transactional
    @Loggable("관리자 뉴스 동기화")
    public void syncLatestNews() {
        newsSyncService.syncLatestNews();
    }
}

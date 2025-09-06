package com.freedom.scrap.domain.service;

import com.freedom.common.logging.Loggable;
import com.freedom.scrap.domain.entity.NewsScrap;
import com.freedom.scrap.infra.NewsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindNewsScrapService {
    
    private final NewsScrapRepository newsScrapRepository;
    
    /**
     * 사용자별 스크랩한 뉴스 목록 조회
     * 
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 뉴스 스크랩 목록
     */
    @Loggable("사용자 뉴스 스크랩 목록 조회")
    public Page<NewsScrap> findNewsScrapsByUserId(Long userId, Pageable pageable) {
        return newsScrapRepository.findByUserIdOrderByScrappedDateDesc(userId, pageable);
    }
    
    /**
     * 사용자의 총 스크랩 수 조회
     * 
     * @param userId 사용자 ID
     * @return 스크랩 수
     */
    public long countNewsScrapsByUserId(Long userId) {
        return newsScrapRepository.countByUserId(userId);
    }
}

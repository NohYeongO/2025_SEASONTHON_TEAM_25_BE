package com.freedom.news.domain.service;

import com.freedom.admin.news.domain.service.NewsPersistenceService;
import com.freedom.admin.news.application.dto.NewsArticleDto;
import com.freedom.news.domain.entity.NewsArticle;
import com.freedom.news.infra.repository.NewsArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsPersistenceService 단위 테스트")
class NewsPersistenceServiceTest {

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @InjectMocks
    private NewsPersistenceService newsPersistenceService;

    @Test
    @DisplayName("신규 뉴스 저장 성공")
    void saveNewArticles_Success() {
        // given
        List<NewsArticleDto> newArticles = List.of(
            createMockNewsArticleDto("news001", "첫 번째 뉴스"),
            createMockNewsArticleDto("news002", "두 번째 뉴스")
        );

        given(newsArticleRepository.saveAll(anyList())).willReturn(List.of());

        // when
        newsPersistenceService.saveNewArticles(newArticles);

        // then
        verify(newsArticleRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("뉴스 업데이트 성공")
    void updateArticles_Success() {
        // given
        NewsArticleDto updateDto = createMockNewsArticleDto("news001", "업데이트된 뉴스");
        List<NewsArticleDto> updateArticles = List.of(updateDto);

        NewsArticle existingArticle = NewsArticle.builder()
                .newsItemId("news001")
                .title("기존 제목")
                .contentsStatus("U")
                .modifyId(1)
                .groupingCode("policy")
                .contentsType("H")
                .dataContents("<p>기존 내용</p>")
                .plainTextContent("기존 내용")
                .contentHash("old_hash")
                .build();

        given(newsArticleRepository.findByNewsItemId("news001"))
            .willReturn(Optional.of(existingArticle));
        given(newsArticleRepository.save(any(NewsArticle.class)))
            .willReturn(existingArticle);

        // when
        newsPersistenceService.updateArticles(updateArticles);

        // then
        verify(newsArticleRepository).findByNewsItemId("news001");
        verify(newsArticleRepository).save(any(NewsArticle.class));
    }

    @Test
    @DisplayName("뉴스 업데이트 실패 - 기존 뉴스 없음")
    void updateArticles_Fail_NewsNotFound() {
        // given
        NewsArticleDto updateDto = createMockNewsArticleDto("news999", "존재하지 않는 뉴스");
        List<NewsArticleDto> updateArticles = List.of(updateDto);

        given(newsArticleRepository.findByNewsItemId("news999"))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> newsPersistenceService.updateArticles(updateArticles))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("기존 뉴스가 존재하지 않습니다");

        verify(newsArticleRepository).findByNewsItemId("news999");
    }

    private NewsArticleDto createMockNewsArticleDto(String newsItemId, String title) {
        return NewsArticleDto.builder()
                .newsItemId(newsItemId)
                .title(title)
                .contentsStatus("U")
                .modifyId(1)
                .modifyDate(LocalDateTime.now())
                .approveDate(LocalDateTime.now().minusHours(1))
                .approverName("테스트승인자")
                .groupingCode("policy")
                .subTitle1("부제목")
                .contentsType("H")
                .dataContents("<p>" + title + " 내용</p>")
                .plainTextContent(title + " 내용")
                .ministerCode("TEST")
                .thumbnailUrl("https://example.com/thumb.jpg")
                .originalUrl("https://example.com/news/" + newsItemId)
                .aiSummary("AI 생성 요약")
                .build();
    }
}

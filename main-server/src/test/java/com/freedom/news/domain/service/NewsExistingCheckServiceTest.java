package com.freedom.news.domain.service;

import com.freedom.news.application.dto.ExistingNewsDto;
import com.freedom.news.domain.result.NewsClassificationResult;
import com.freedom.news.infra.client.response.NewsItem;
import com.freedom.news.infra.repository.NewsArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsExistingCheckService 단위 테스트")
class NewsExistingCheckServiceTest {

    @Mock
    private NewsArticleRepository newsArticleRepository;

    @InjectMocks
    private NewsExistingCheckService newsExistingCheckService;

    @Test
    @DisplayName("신규 뉴스 분류 성공")
    void classifyNews_Success_NewNews() {
        // given
        List<NewsItem> newsList = List.of(
            createMockNewsItem("news001", 1, "<p>새로운 내용</p>"),
            createMockNewsItem("news002", 1, "<p>또 다른 새로운 내용</p>")
        );

        // DB에 기존 뉴스 없음
        given(newsArticleRepository.findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).willReturn(List.of());

        // when
        NewsClassificationResult result = newsExistingCheckService.classifyNews(newsList);

        // then
        assertThat(result.getNewNews()).hasSize(2);
        assertThat(result.getUpdatedNews()).isEmpty();
        assertThat(result.hasNewsToProcess()).isTrue();

        verify(newsArticleRepository).findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("업데이트된 뉴스 분류 성공 - modifyId 증가")
    void classifyNews_Success_UpdatedNewsByModifyId() {
        // given
        List<NewsItem> newsList = List.of(
            createMockNewsItem("news001", 2, "<p>기존 내용</p>") // modifyId 증가
        );

        // DB에 기존 뉴스 있음 (modifyId = 1)
        List<ExistingNewsDto> existingNews = List.of(
            new ExistingNewsDto("news001", 1, "existing_hash")
        );
        given(newsArticleRepository.findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).willReturn(existingNews);

        // when
        NewsClassificationResult result = newsExistingCheckService.classifyNews(newsList);

        // then
        assertThat(result.getNewNews()).isEmpty();
        assertThat(result.getUpdatedNews()).hasSize(1);
        assertThat(result.getUpdatedNews().get(0).getNewsItemId()).isEqualTo("news001");
        assertThat(result.hasNewsToProcess()).isTrue();
    }

    @Test
    @DisplayName("업데이트된 뉴스 분류 성공 - 컨텐츠 변경")
    void classifyNews_Success_UpdatedNewsByContentChange() {
        // given
        List<NewsItem> newsList = List.of(
            createMockNewsItem("news001", 1, "<p>변경된 내용</p>") // 컨텐츠 변경
        );

        // DB에 기존 뉴스 있음 (같은 modifyId, 다른 해시)
        List<ExistingNewsDto> existingNews = List.of(
            new ExistingNewsDto("news001", 1, "different_hash")
        );
        given(newsArticleRepository.findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).willReturn(existingNews);

        // when
        NewsClassificationResult result = newsExistingCheckService.classifyNews(newsList);

        // then
        assertThat(result.getNewNews()).isEmpty();
        assertThat(result.getUpdatedNews()).hasSize(1);
        assertThat(result.getUpdatedNews().get(0).getNewsItemId()).isEqualTo("news001");
    }

    @Test
    @DisplayName("변경되지 않은 뉴스 필터링")
    void classifyNews_Success_NoChanges() {
        // given
        String content = "<p>동일한 내용</p>";
        List<NewsItem> newsList = List.of(
            createMockNewsItem("news001", 1, content)
        );

        // DB에 동일한 뉴스 있음
        String expectedHash = com.freedom.common.util.HashUtil.sha256(content);
        List<ExistingNewsDto> existingNews = List.of(
            new ExistingNewsDto("news001", 1, expectedHash)
        );
        given(newsArticleRepository.findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).willReturn(existingNews);

        // when
        NewsClassificationResult result = newsExistingCheckService.classifyNews(newsList);

        // then
        assertThat(result.getNewNews()).isEmpty();
        assertThat(result.getUpdatedNews()).isEmpty();
        assertThat(result.hasNewsToProcess()).isFalse();
    }

    @Test
    @DisplayName("혼합 케이스 - 신규, 업데이트, 변경없음")
    void classifyNews_Success_MixedCases() {
        // given
        List<NewsItem> newsList = List.of(
            createMockNewsItem("news001", 1, "<p>새로운 뉴스</p>"), // 신규
            createMockNewsItem("news002", 2, "<p>업데이트된 뉴스</p>"), // 업데이트 (modifyId)
            createMockNewsItem("news003", 1, "<p>변경된 내용</p>"), // 업데이트 (내용)
            createMockNewsItem("news004", 1, "<p>동일한 내용</p>") // 변경없음
        );

        // DB에 일부 기존 뉴스 있음
        String unchangedHash = com.freedom.common.util.HashUtil.sha256("<p>동일한 내용</p>");
        List<ExistingNewsDto> existingNews = List.of(
            new ExistingNewsDto("news002", 1, "old_hash"), // modifyId 증가됨
            new ExistingNewsDto("news003", 1, "different_hash"), // 내용 변경됨
            new ExistingNewsDto("news004", 1, unchangedHash) // 변경없음
        );
        given(newsArticleRepository.findTodayNewsItemIdAndModifyIdAndHashByNewsItemIdIn(
            anyList(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).willReturn(existingNews);

        // when
        NewsClassificationResult result = newsExistingCheckService.classifyNews(newsList);

        // then
        assertThat(result.getNewNews()).hasSize(1);
        assertThat(result.getNewNews().get(0).getNewsItemId()).isEqualTo("news001");
        
        assertThat(result.getUpdatedNews()).hasSize(2);
        assertThat(result.getUpdatedNews())
            .extracting(NewsItem::getNewsItemId)
            .containsExactlyInAnyOrder("news002", "news003");

        assertThat(result.hasNewsToProcess()).isTrue();
    }

    private NewsItem createMockNewsItem(String newsItemId, Integer modifyId, String content) {
        return NewsItem.builder()
                .newsItemId(newsItemId)
                .modifyId(modifyId)
                .dataContents(content)
                .title("테스트 제목")
                .contentsStatus("U")
                .groupingCode("policy")
                .build();
    }
}

package com.freedom.news.api;

import com.freedom.auth.domain.User;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.domain.UserStatus;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.common.security.JwtProvider;
import com.freedom.common.test.TestContainerConfig;
import com.freedom.news.api.response.NewsDetailResponse;
import com.freedom.news.infra.repository.NewsArticleRepository;
import com.freedom.news.infra.repository.NewsContentBlockRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NewsController API 통합 테스트")
@Sql("/test-data.sql")
class NewsControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    @Autowired
    private NewsContentBlockRepository newsContentBlockRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private Long userId;

    private String getAuthorizationHeader() {
        String accessToken = jwtProvider.createAccessToken(userId);
        return "Bearer " + accessToken;
    }

    @BeforeEach
    void setUp() {
        User user = User.builder().email("test@exmple.com").password("test12345@").role(UserRole.USER).status(UserStatus.ACTIVE).build();
        User saveUser = userJpaRepository.save(user);
        userId = saveUser.getId();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
        newsContentBlockRepository.deleteAll();
        newsArticleRepository.deleteAll();
    }

    @Test
    @DisplayName("뉴스 리스트 조회 성공 - 기본 페이징")
    void getNewsList_Success_DefaultPaging() {
        webTestClient.get()
                .uri("/api/news")
                .header("Authorization", getAuthorizationHeader())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[0].title").isEqualTo("오늘 정책 뉴스 제목")
                .jsonPath("$.content[1].title").isEqualTo("어제 정책 뉴스 제목")
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    @DisplayName("뉴스 리스트 조회 성공 - 커스텀 페이징")
    void getNewsList_Success_CustomPaging() {
        webTestClient.get()
                .uri("/api/news?page=0&size=1")
                .header("Authorization", getAuthorizationHeader())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[0].title").isEqualTo("오늘 정책 뉴스 제목")
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.size").isEqualTo(1)
                .jsonPath("$.number").isEqualTo(0);
    }

    @Test
    @DisplayName("뉴스 상세 조회 성공")
    void getNewsDetail_Success() {
        webTestClient.get()
                .uri("/api/news/1")
                .header("Authorization", getAuthorizationHeader())
                .exchange()
                .expectStatus().isOk()
                .expectBody(NewsDetailResponse.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(1L);
                    assertThat(response.getNewsItemId()).isEqualTo("news001");
                    assertThat(response.getTitle()).isEqualTo("어제 정책 뉴스 제목");
                    assertThat(response.getAiSummary()).isEqualTo("AI 요약: 어제 중요한 정책 발표");
                    assertThat(response.getContentBlocks()).hasSize(2);
                    
                    assertThat(response.getContentBlocks().get(0).getBlockType()).isEqualTo("TEXT");
                    assertThat(response.getContentBlocks().get(0).getPlainContent()).isEqualTo("첫 번째 텍스트 블록");
                    assertThat(response.getContentBlocks().get(1).getBlockType()).isEqualTo("IMAGE");
                    assertThat(response.getContentBlocks().get(1).getUrl()).isEqualTo("https://example.com/test.jpg");
                });
    }

    @Test
    @DisplayName("뉴스 상세 조회 실패 - 존재하지 않는 뉴스")
    void getNewsDetail_Fail_NotFound() {
        webTestClient.get()
                .uri("/api/news/999999")
                .header("Authorization", getAuthorizationHeader())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(message -> 
                    assertThat(message.toString()).contains("뉴스를 찾을 수 없습니다.")
                );
    }
}

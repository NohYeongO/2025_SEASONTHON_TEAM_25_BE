package com.freedom.admin.news.application.facade;

import com.freedom.admin.news.application.dto.NewsArticleDto;
import com.freedom.admin.news.domain.result.NewsClassificationResult;
import com.freedom.admin.news.domain.model.ProcessedNews;
import com.freedom.admin.news.domain.service.NewsContentProcessingService;
import com.freedom.admin.news.domain.service.NewsExistingCheckService;
import com.freedom.admin.news.domain.service.NewsPersistenceService;
import com.freedom.admin.news.infra.client.OpenAiNewsSummaryClient;
import com.freedom.admin.news.infra.client.PolicyNewsClient;
import com.freedom.admin.news.infra.client.response.NewsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsFacade {

    private final PolicyNewsClient policyNewsClient;
    private final NewsContentProcessingService newsContentProcessingService;
    private final NewsExistingCheckService newsExistingCheckService;
    private final OpenAiNewsSummaryClient openAiNewsSummaryClient;
    private final NewsPersistenceService newsPersistenceService;

    @Transactional
    public void newsCollection() {
        List<NewsItem> newsList = policyNewsClient.getNewsAPI();
        if (newsList.isEmpty()) return;

        NewsClassificationResult classification = newsExistingCheckService.classifyNews(newsList);
        if (!classification.hasNewsToProcess()) return;

        List<NewsArticleDto> newArticles    = toArticlesDistinctById(classification.getNewNews());
        List<NewsArticleDto> updatedArticles = toArticlesDistinctById(classification.getUpdatedNews());

        List<NewsArticleDto> summarizedNew     = summarizeAll(newArticles);
        List<NewsArticleDto> summarizedUpdated = summarizeAll(updatedArticles);

        if (!summarizedNew.isEmpty()) {
            newsPersistenceService.saveNewArticles(summarizedNew);
        }
        if (!summarizedUpdated.isEmpty()) {
            newsPersistenceService.updateArticles(summarizedUpdated);
        }
    }

    private List<NewsArticleDto> toArticlesDistinctById(List<NewsItem> items) {
        if (items == null || items.isEmpty()) return List.of();

        Map<String, NewsItem> dedup = items.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        NewsItem::getNewsItemId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return dedup.values().stream()
                .map(this::processNewsItem)
                .toList();
    }

    private List<NewsArticleDto> summarizeAll(List<NewsArticleDto> articles) {
        if (articles == null || articles.isEmpty()) return List.of();
        return articles.stream()
                .map(a -> a.withAiSummary(openAiNewsSummaryClient.summarize(a.getPlainTextContent())))
                .toList();
    }

    private NewsArticleDto processNewsItem(NewsItem newsItem) {
        ProcessedNews processedNews = newsContentProcessingService.processHtmlContent(newsItem.getDataContents());
        return NewsArticleDto.from(newsItem, processedNews);
    }
}

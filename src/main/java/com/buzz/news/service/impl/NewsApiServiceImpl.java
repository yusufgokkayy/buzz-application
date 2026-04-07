package com.buzz.news.service.impl;

import com.buzz.news.entity.Category;
import com.buzz.news.entity.News;
import com.buzz.news.repository.CategoryRepository;
import com.buzz.news.service.NewsApiService;
import com.buzz.news.service.NewsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsApiServiceImpl implements NewsApiService {

    private final NewsProducer newsProducer;
    private final CategoryRepository categoryRepository;
    private final WebClient newsApiWebClient;

    public NewsApiServiceImpl(NewsProducer newsProducer,
                              CategoryRepository categoryRepository,
                              @Qualifier("newsApiWebClient") WebClient newsApiWebClient) {
        this.newsProducer = newsProducer;
        this.categoryRepository = categoryRepository;
        this.newsApiWebClient = newsApiWebClient;
    }

    @Override
    public void fetchAndPublishNews() {
        List<News> newsList = fetchNewsFromApi();
        if (newsList.isEmpty()) {
            log.info("API'den haber gelmedi");
            return;
        }
        newsList.forEach(newsProducer::publishNews);
        log.info("📬 {} haber Redis'e gönderildi", newsList.size());
    }

    private List<News> fetchNewsFromApi() {
        Map<String, Object> response = newsApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/everything")
                        .queryParam("q", "turkey")
                        .queryParam("language", "tr")
                        .queryParam("pageSize", 20)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null || !response.containsKey("articles")) {
            log.warn("API response boş geldi: {}", response);
            return List.of();
        }

        List<Map<String, Object>> articles =
                (List<Map<String, Object>>) response.get("articles");

        if (articles == null || articles.isEmpty()) {
            log.warn("Articles boş geldi");
            return List.of();
        }

        return articles.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    private News mapToEntity(Map<String, Object> article) {
        Map<String, Object> source = (Map<String, Object>) article.get("source");

        News news = new News();
        news.setTitle((String) article.get("title"));
        news.setSourceUrl((String) article.get("url"));
        news.setImageUrl((String) article.get("urlToImage"));
        news.setAuthor((String) article.get("author"));
        news.setSourceName((String) source.get("name"));
        news.setPublishedAt(parseDate((String) article.get("publishedAt")));

        Category defaultCategory = categoryRepository.findById(1L).orElse(null);
        news.setCategory(defaultCategory);

        return news;
    }

    private LocalDateTime parseDate(String publishedAt) {
        if (publishedAt == null) return LocalDateTime.now();
        return LocalDateTime.parse(publishedAt, DateTimeFormatter.ISO_DATE_TIME);
    }
}
package com.buzz.news.service.impl;

import com.buzz.news.entity.Category;
import com.buzz.news.entity.News;
import com.buzz.news.repository.CategoryRepository;
import com.buzz.news.repository.NewsRepository;
import com.buzz.news.service.NewsConsumer;
import com.buzz.news.service.SummaryService;
import com.buzz.news.util.ArticleScraper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsConsumerImpl implements NewsConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SummaryService summaryService;
    private final ArticleScraper articleScraper;
    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;

    private static final String STREAM_KEY = NewsProducerImpl.STREAM_KEY;
    private static final String GROUP_NAME = "news-group";
    private static final String CONSUMER   = "consumer-1";

    @Override
    @PostConstruct
    public void initGroup() {
        try {
            redisTemplate.opsForStream()
                    .createGroup(STREAM_KEY, ReadOffset.from("0"), GROUP_NAME);
            log.info("✅ Redis consumer group oluşturuldu");
        } catch (Exception e) {
            log.info("ℹ️ Consumer group zaten mevcut");
        }
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void consumeNews() {
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                .read(Consumer.from(GROUP_NAME, CONSUMER),
                        StreamReadOptions.empty().count(10),
                        StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));

        if (messages == null || messages.isEmpty()) return;

        // Döngü dışına alındı — 20 haber için 1 sorgu
        Category defaultCategory = categoryRepository.findById(1L).orElse(null);

        for (MapRecord<String, Object, Object> message : messages) {
            try {
                Map<Object, Object> data = message.getValue();

                String sourceUrl = (String) data.get("sourceUrl");
                String title     = (String) data.get("title");

                // 1. İçeriği scrape et
                String content = articleScraper.scrapeArticle(sourceUrl);

                // 2. Özetle
                String summary = summaryService.generateSummary(content);

                // 3. DB'ye kaydet
                News news = News.builder()
                        .title(title)
                        .content(content)
                        .summary(summary)
                        .imageUrl((String)  data.get("imageUrl"))
                        .sourceUrl(sourceUrl)
                        .sourceName((String) data.get("sourceName"))
                        .author((String)    data.get("author"))
                        .category(defaultCategory)
                        .isPublished(true)
                        .build();

                newsRepository.save(news);

                // 4. ACK
                redisTemplate.opsForStream()
                        .acknowledge(STREAM_KEY, GROUP_NAME, message.getId());

                log.info("✅ Kaydedildi: {}", title);

            } catch (Exception e) {
                log.error("❌ İşlenemedi: {}", e.getMessage());
            }
        }
    }
}
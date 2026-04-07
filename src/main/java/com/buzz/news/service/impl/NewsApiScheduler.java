package com.buzz.news.service.impl;

import com.buzz.news.service.NewsApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
@Slf4j
public class NewsApiScheduler {

    private final NewsApiService newsApiService;

    @Scheduled(fixedDelay = 60000)
    public void fetchAndPublish() {
        log.info("🔄 Haberler çekiliyor...");
        newsApiService.fetchAndPublishNews();
    }
}
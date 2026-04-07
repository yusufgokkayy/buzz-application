package com.buzz.news.service.impl;

import com.buzz.news.entity.News;
import com.buzz.news.service.NewsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsProducerImpl implements NewsProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    public static final String STREAM_KEY = "news-stream";

    @Override
    public void publishNews(News news) {
        Map<String, String> message = new HashMap<>();
        message.put("title",      news.getTitle());
        message.put("content",    news.getContent() != null ? news.getContent() : "");
        message.put("imageUrl",   news.getImageUrl() != null ? news.getImageUrl() : "");
        message.put("sourceUrl",  news.getSourceUrl() != null ? news.getSourceUrl() : "");
        message.put("sourceName", news.getSourceName() != null ? news.getSourceName() : "");
        message.put("author",     news.getAuthor() != null ? news.getAuthor() : "");

        redisTemplate.opsForStream().add(STREAM_KEY, message);
        log.info("📨 Redis Stream'e eklendi: {}", news.getTitle());
    }
}

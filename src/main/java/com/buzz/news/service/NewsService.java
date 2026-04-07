package com.buzz.news.service;

import com.buzz.news.dto.NewsRequest;
import com.buzz.news.dto.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {

    NewsResponse createNews(NewsRequest request);

    Page<NewsResponse> getAllNews(Pageable pageable);

    Page<NewsResponse> getNewsByCategory(String categorySlug, Pageable pageable);

    Page<NewsResponse> getTrendingNews(Pageable pageable);

    Page<NewsResponse> searchNews(String keyword, Pageable pageable);

    NewsResponse getNewsById(Long id);

    NewsResponse updateNews(Long id, NewsRequest request);

    void deleteNews(Long id);
}

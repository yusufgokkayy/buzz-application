package com.buzz.news.service.impl;

import com.buzz.common.exception.ResourceNotFoundException;
import com.buzz.news.dto.NewsRequest;
import com.buzz.news.dto.NewsResponse;
import com.buzz.news.entity.Category;
import com.buzz.news.entity.News;
import com.buzz.news.repository.CategoryRepository;
import com.buzz.news.repository.NewsRepository;
import com.buzz.news.service.NewsService;
import com.buzz.news.service.SummaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final SummaryService summaryService;

    @Override
    @Transactional
    public NewsResponse createNews(NewsRequest request) {
        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new ResourceNotFoundException("" +
                        "Kategori bulunamadı: " + request.getCategorySlug()));


        // ======= AI ÖZET EKLEME =========

        String summary = request.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = summaryService.generateSummary(request.getContent());
        }

        System.out.println("Summary length: " + summary.length());

        // System.out.println("[FINAL SUMMARY]: (" + (summary != null ? summary.length() : 0) + ") " + summary);

        News news = News.builder()
                .title(request.getTitle())
                .summary(summary)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .sourceUrl(request.getSourceUrl())
                .sourceName(request.getSourceName())
                .author(request.getAuthor())
                .category(category)
                .trendScore(request.getTrendScore() != null ? request.getTrendScore() : 0)
                .build();

        newsRepository.save(news);

        return mapToResponse(news);
    }

    @Override
    public Page<NewsResponse> getAllNews(Pageable pageable) {
        return newsRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<NewsResponse> getNewsByCategory(String categorySlug, Pageable pageable) {
        return newsRepository.findByCategorySlugAndIsPublishedTrue(categorySlug, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<NewsResponse> getTrendingNews(Pageable pageable) {
        return newsRepository.findTrendingNews(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<NewsResponse> searchNews(String keyword, Pageable pageable) {
        return newsRepository.searchByKeyword(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haber bulunamadı, id: " + id));

        // View count artır
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);

        return mapToResponse(news);
    }

    @Override
    @Transactional
    public NewsResponse updateNews(Long id, NewsRequest request) {

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haber bulunamadı, id: " + id));

        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kategori bulunamadı: " + request.getCategorySlug()));

        String summary = request.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = summaryService.generateSummary(request.getContent());
        }

        System.out.println("[FINAL SUMMARY]: (" + (summary != null ? summary.length() : 0) + ") " + summary);

        news.setSummary(summary);
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setSourceUrl(request.getSourceUrl());
        news.setSourceName(request.getSourceName());
        news.setAuthor(request.getAuthor());
        news.setCategory(category);

        if (request.getTrendScore() != null) {
            news.setTrendScore(request.getTrendScore());
        }

        newsRepository.save(news);

        return mapToResponse(news);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haber bulubamadı, id: " + id));

        news.setIsPublished(false);
        newsRepository.save(news);
    }

    // ===== HELPER =====

    private NewsResponse mapToResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .summary(news.getSummary())
                .content(news.getContent())
                .imageUrl(news.getImageUrl())
                .sourceUrl(news.getSourceUrl())
                .sourceName(news.getSourceName())
                .author(news.getAuthor())
                .categoryName(news.getCategory() != null ? news.getCategory().getName() : null)
                .categorySlug(news.getCategory() != null ? news.getCategory().getSlug() : null)
                .trendScore(news.getTrendScore())
                .viewCount(news.getViewCount())
                .likeCount(news.getLikeCount())
                .bookmarkCount(news.getBookmarkCount())
                .publishedAt(news.getPublishedAt())
                .createdAt(news.getCreatedAt())
                .build();
    }
}

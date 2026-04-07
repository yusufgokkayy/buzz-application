package com.buzz.news.repository;

import com.buzz.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    Page<News> findByCategorySlugAndIsPublishedTrue(String slug, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isPublished = true " +
            "ORDER BY n.trendScore DESC, n.publishedAt DESC")
    Page<News> findTrendingNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isPublished = true AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsBySourceUrl(String sourceUrl);
}
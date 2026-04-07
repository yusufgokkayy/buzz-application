package com.buzz.news.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private String sourceUrl;
    private String sourceName;
    private String author;
    private String categoryName;
    private String categorySlug;
    private Integer trendScore;
    private Long viewCount;
    private Long likeCount;
    private Long bookmarkCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

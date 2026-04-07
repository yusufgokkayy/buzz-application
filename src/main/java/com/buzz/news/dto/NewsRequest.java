package com.buzz.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 300, message = "Başlık en fazla 300 karakter olabilir")
    private String title;

    @Size(max = 500, message = "Özet en fazla 500 karakter olabilir")
    private String summary;

    private String content;

    private String imageUrl;

    private String sourceUrl;

    private String sourceName;

    private String author;

    @NotBlank(message = "Kategori belirtilmeli")
    private String categorySlug;

    private Integer trendScore;
}

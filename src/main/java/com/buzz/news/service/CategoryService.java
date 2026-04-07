package com.buzz.news.service;

import com.buzz.news.dto.CategoryRequest;
import com.buzz.news.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllActiveCategories();

    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}

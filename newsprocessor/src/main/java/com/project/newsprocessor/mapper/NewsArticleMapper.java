package com.project.newsprocessor.mapper;

import com.project.newsprocessor.entity.NewsArticle;

public interface NewsArticleMapper<T> {
    NewsArticle map(T sourceDto);
    String scrapeContent(String url);
}

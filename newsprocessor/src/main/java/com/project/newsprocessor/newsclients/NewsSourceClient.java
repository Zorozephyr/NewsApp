package com.project.newsprocessor.newsclients;

import com.project.newsprocessor.entity.NewsArticle;

import java.util.List;

public interface NewsSourceClient {
    List<NewsArticle> fetchArticles();
}

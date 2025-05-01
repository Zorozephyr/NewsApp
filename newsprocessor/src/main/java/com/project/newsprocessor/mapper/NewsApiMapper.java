package com.project.newsprocessor.mapper;

import com.project.newsprocessor.dto.NewsApiArticleDto;
import com.project.newsprocessor.dto.NewsApiResponseDto;
import com.project.newsprocessor.entity.NewsArticle;
import com.project.newsprocessor.utils.WebScraper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class NewsApiMapper implements NewsArticleMapper<NewsApiArticleDto> {
    @Override
    public NewsArticle map(NewsApiArticleDto dto) {
        NewsArticle article = new NewsArticle();
        article.setTitle(dto.getTitle());
        article.setSourceName(dto.getSource() != null ? dto.getSource().getName() : "Unknown");
        article.setAuthor(dto.getAuthor());
        article.setDescription(dto.getDescription());
        article.setContent(this.scrapeContent(dto.getUrl()));
        article.setUrl(dto.getUrl());
        article.setPublishedAt(LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME));
        article.setCreatedAt(LocalDateTime.now());
        return article;
    }

    @Override
    public String scrapeContent(String url) {
        return WebScraper.scrapeFullArticleContent(url);
    }
}
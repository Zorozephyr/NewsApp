package com.project.newsprocessor.mapper;


import com.project.newsprocessor.dto.NewsEvent;
import com.project.newsprocessor.entity.NewsArticle;

public class NewsArticleToNewsEventMapper {

    public static NewsEvent toNewsEvent(NewsArticle article) {
        if (article == null) {
            return null;
        }

        NewsEvent event = new NewsEvent();
        event.setId(article.getId());
        event.setTitle(article.getTitle());
        event.setSourceName(article.getSourceName());
        event.setAuthor(article.getAuthor());
        event.setDescription(article.getDescription());
        event.setContent(article.getContent());
        event.setUrl(article.getUrl());
        event.setPublishedAt(article.getPublishedAt());
        event.setCreatedAt(article.getCreatedAt());

        return event;
    }

}

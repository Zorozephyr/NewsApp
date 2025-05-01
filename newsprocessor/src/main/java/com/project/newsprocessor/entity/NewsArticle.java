package com.project.newsprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewsArticle {

    @Id
    private String id;
    private String title;
    private String sourceName;
    private String author;
    private String description;
    private String content;
    private String url;
    private String urlToImage;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

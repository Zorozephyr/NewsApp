package com.project.newsprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewsApiResponseDto {

    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    // Getters and Setters

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class Source {
        private String id;
        private String name;

        // Getters and Setters
    }
}

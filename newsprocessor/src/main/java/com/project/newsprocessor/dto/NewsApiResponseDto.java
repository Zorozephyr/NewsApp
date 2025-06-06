package com.project.newsprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsApiResponseDto {
    private String status;
    private int totalResults;
    private List<NewsApiArticleDto> articles;
}

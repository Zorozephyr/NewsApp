package com.project.newsprocessor.service;

import com.project.newsprocessor.dto.NewsApiArticlesDto;
import com.project.newsprocessor.dto.NewsApiResponseDto;
import com.project.newsprocessor.entity.NewsArticle;
import com.project.newsprocessor.repository.NewsProcessingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NewsProcessingService {

    private static final String Kafka_Topic = "news.fetched";

    private NewsProcessingRepository newsProcessingRepository;

    private RestTemplate restTemplate;

    @Autowired
    public NewsProcessingService(NewsProcessingRepository newsProcessingRepository, RestTemplate restTemplate){
        this.newsProcessingRepository = newsProcessingRepository;
        this.restTemplate = restTemplate;
    }

    public void fetchNews(){

        String newsApiUrl = "https://newsapi.org/v2/top-headlines?country=us&apiKey=d93b0adf8edd4857bdb03a58d26b335a&pageSize=10&page=1";

        NewsApiArticlesDto response = restTemplate.getForObject(newsApiUrl, NewsApiArticlesDto.class);

        if(response!=null && response.getArticles()!=null){
            for(NewsApiResponseDto dto: response.getArticles()){
                NewsArticle article = new NewsArticle();
                article.setTitle(dto.getTitle());
                article.setSourceName(dto.getSource() != null ? dto.getSource().getName() : "Unknown");
                article.setAuthor(dto.getAuthor());
                article.setDescription(dto.getDescription());
                article.setContent(dto.getContent());
                article.setUrl(dto.getUrl());
                article.setUrlToImage(dto.getUrlToImage());
                article.setPublishedAt(LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME));
                article.setCreatedAt(LocalDateTime.now());


                newsProcessingRepository.save(article);
            }
        }
    }
}

package com.project.newsprocessor.service;

import com.project.newsprocessor.dto.NewsApiResponseDto;
import com.project.newsprocessor.dto.NewsApiArticleDto;
import com.project.newsprocessor.entity.NewsArticle;
import com.project.newsprocessor.newsclients.NewsSourceClient;
import com.project.newsprocessor.repository.NewsProcessingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NewsProcessingService {

    private static final String Kafka_Topic = "news.fetched";

    @Autowired
    private List<NewsSourceClient> clients;
    private NewsProcessingRepository newsProcessingRepository;

    private RestTemplate restTemplate;

    @Autowired
    public NewsProcessingService(NewsProcessingRepository newsProcessingRepository, RestTemplate restTemplate){
        this.newsProcessingRepository = newsProcessingRepository;
        this.restTemplate = restTemplate;
    }

    public void fetchNews(){

        for (NewsSourceClient client : clients) {
            List<NewsArticle> articles = client.fetchArticles();

            for (NewsArticle article : articles) {
                newsProcessingRepository.save(article);
            }
        }
    }
}

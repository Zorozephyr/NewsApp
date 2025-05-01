package com.project.newsprocessor.newsclients;

import com.project.newsprocessor.dto.NewsApiResponseDto;
import com.project.newsprocessor.entity.NewsArticle;
import com.project.newsprocessor.mapper.NewsApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsApiClient implements NewsSourceClient {
    @Autowired
    private Environment environment;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NewsApiMapper mapper;

    @Value("${news-api.api.key}")
    private String API_KEY;
    private final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey="+this.API_KEY+"&pageSize=10&page=2";
    @Override
    public List<NewsArticle> fetchArticles() {
        NewsApiResponseDto response = restTemplate.getForObject(API_URL, NewsApiResponseDto.class);
        return response.getArticles().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }
}

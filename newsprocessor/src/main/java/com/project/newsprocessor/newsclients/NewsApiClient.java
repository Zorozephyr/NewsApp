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

    @Value("${news-api.api.url}")
    private String BASE_URL;

    @Value("${news-api.api.query-params}")
    private String QUERY_PARAMS;

    @Override
    public List<NewsArticle> fetchArticles() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API_KEY must not be empty");
        }

        String apiUrl = BASE_URL + API_KEY + QUERY_PARAMS ;

        NewsApiResponseDto response = restTemplate.getForObject(apiUrl, NewsApiResponseDto.class);
        return response.getArticles().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }
}

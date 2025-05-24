package com.project.newsprocessor.service;

import com.project.newsprocessor.dto.NewsApiResponseDto;
import com.project.newsprocessor.dto.NewsApiArticleDto;
import com.project.newsprocessor.entity.NewsArticle;
import com.project.newsprocessor.mapper.NewsArticleToNewsEventMapper;
import com.project.newsprocessor.newsclients.NewsSourceClient;
import com.project.newsprocessor.repository.NewsProcessingRepository;
import com.project.newsprocessor.service.kafka.NewsProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class NewsProcessingService {

    private static final String Kafka_Topic = "news.fetched";

    @Autowired
    private List<NewsSourceClient> clients;
    private NewsProcessingRepository newsProcessingRepository;
    private NewsProducerService newsProducerService;

    private RestTemplate restTemplate;

    @Autowired
    public NewsProcessingService(NewsProcessingRepository newsProcessingRepository, RestTemplate restTemplate, NewsProducerService newsProducerService){
        this.newsProcessingRepository = newsProcessingRepository;
        this.restTemplate = restTemplate;
        this.newsProducerService = newsProducerService;
    }

    public void fetchNews(){

        for (NewsSourceClient client : clients) {
            List<NewsArticle> articles = client.fetchArticles();

            for (NewsArticle article : articles) {
                String contentHash = DigestUtils.sha256Hex(article.getContent());
                article.setContentHash(contentHash);
                boolean exists = newsProcessingRepository.existsByUrl(article.getUrl()) ||
                        newsProcessingRepository.existsByContentHash(contentHash);

                if (!exists) {
                    newsProcessingRepository.save(article);
                    log.info("Article has been saved::" + article);
                    newsProducerService.sendNewsEvent(NewsArticleToNewsEventMapper.toNewsEvent(article));
                    log.info("News event has been published to news.fetched topic");
                } else {
                    System.out.println("Duplicate article, not saving.");
                }
            }
        }
    }

}

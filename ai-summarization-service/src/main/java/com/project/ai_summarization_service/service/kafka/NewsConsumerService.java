package com.project.ai_summarization_service.service.kafka;

import com.project.ai_summarization_service.dto.NewsEvent;
import com.project.ai_summarization_service.entity.Article;
import com.project.ai_summarization_service.repository.ArticleRepository;
import com.project.ai_summarization_service.service.ArticleProcessingService;
import com.project.ai_summarization_service.service.NewsProcessingService;
import com.project.ai_summarization_service.service.gpt.GPTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NewsConsumerService {

    private NewsProcessingService newsProcessingService;


    @Autowired
    public NewsConsumerService(NewsProcessingService newsProcessingService) {
        this.newsProcessingService = newsProcessingService;
    }


    @KafkaListener(topics = "news.fetched", groupId = "news-group")
    public void consumerNewsEvent(ConsumerRecord<String, NewsEvent> record) {
        log.info("=== KAFKA MESSAGE RECEIVED ===");
        log.info("Topic: {}", record.topic());
        log.info("Partition: {}", record.partition());
        log.info("Offset: {}", record.offset());
        log.info("Key: {}", record.key());
        log.info("Timestamp: {}", record.timestamp());

        try {
            NewsEvent newsEvent = record.value();

            if (newsEvent == null) {
                log.warn("Received null NewsEvent at offset {}", record.offset());
                return;
            }

            log.info("✅ DESERIALIZATION SUCCESSFUL!");
            log.info("NewsEvent ID: {}", newsEvent.getId());
            log.info("NewsEvent Title: {}", newsEvent.getTitle());
            log.info("NewsEvent Source: {}", newsEvent.getSourceName());
            log.info("NewsEvent Author: {}", newsEvent.getAuthor());
            log.info("NewsEvent Published At: {}", newsEvent.getPublishedAt());
            log.info("NewsEvent Created At: {}", newsEvent.getCreatedAt());
            log.info("NewsEvent URL: {}", newsEvent.getUrl());

            // Process the news event
            newsProcessingService.processNewsEvent(newsEvent);

            log.info("✅ Message processing completed successfully");

        } catch (Exception e) {
            log.error("❌ ERROR processing news event from topic {} [partition={}, offset={}]",
                    record.topic(), record.partition(), record.offset());
            log.error("Error details: {}", e.getMessage(), e);
        }

        log.info("=== END KAFKA MESSAGE ===\n");
    }


}
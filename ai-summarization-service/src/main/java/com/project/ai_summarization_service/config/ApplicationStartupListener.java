package com.project.ai_summarization_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartupListener {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("🚀 AI Summarization Service Started Successfully!");
        log.info("📡 Kafka Bootstrap Servers: {}", bootstrapServers);
        log.info("👥 Consumer Group ID: {}", groupId);
        log.info("📥 Listening for messages on topic: news.fetched");
        log.info("🔧 Type mapping configured: com.project.newsprocessor.dto.NewsEvent -> com.project.ai_summarization_service.dto.NewsEvent");
        log.info("⏳ Waiting for Kafka messages...");
    }
}
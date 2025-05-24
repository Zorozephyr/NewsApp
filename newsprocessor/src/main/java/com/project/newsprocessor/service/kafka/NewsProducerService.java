package com.project.newsprocessor.service.kafka;

import com.project.newsprocessor.dto.NewsEvent;
import org.springframework.kafka.support.SendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NewsProducerService {

    private static final String TOPIC = "news.fetched";

    @Autowired
    private KafkaTemplate<String,NewsEvent> kafkaTemplate;

    public void sendNewsEvent(NewsEvent newsEvent){
        CompletableFuture<SendResult<String,NewsEvent>> future = kafkaTemplate.send(TOPIC, newsEvent.getId(), newsEvent);
        future.whenComplete((result,ex) -> {
            if(ex == null){
                log.info("Message sent to topic" + TOPIC);
            }
            else{
                log.error("Failed to send message to topic:" + ex.getMessage());
            }
        });
    }
}

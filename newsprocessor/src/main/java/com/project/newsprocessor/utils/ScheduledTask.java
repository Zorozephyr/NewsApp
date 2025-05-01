package com.project.newsprocessor.utils;

import com.project.newsprocessor.repository.NewsProcessingRepository;
import com.project.newsprocessor.service.NewsProcessingService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTask {


    private NewsProcessingService newsProcessingService;

    @Autowired
    public ScheduledTask(NewsProcessingService newsProcessingService) {
        this.newsProcessingService = newsProcessingService;
    }

    //Scheduled every hour
    @Scheduled(cron = "0 0 * * * ?")
    public void fetchNewsScheduled(){
        log.info("Api has worked");
        newsProcessingService.fetchNews();
        log.info("Api has worked");
    }

    @PostConstruct
    public void fetchNewsPostConstruct(){
        log.info("Api has worked");
        newsProcessingService.fetchNews();
        log.info("Api has worked");
    }
}

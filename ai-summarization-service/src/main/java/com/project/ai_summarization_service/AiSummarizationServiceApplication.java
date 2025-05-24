package com.project.ai_summarization_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AiSummarizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiSummarizationServiceApplication.class, args);
	}

}

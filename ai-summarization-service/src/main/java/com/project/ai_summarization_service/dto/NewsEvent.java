package com.project.ai_summarization_service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewsEvent {
    private String id;
    private String title;
    private String sourceName;
    private String author;
    private String description;
    private String content;
    private String url;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

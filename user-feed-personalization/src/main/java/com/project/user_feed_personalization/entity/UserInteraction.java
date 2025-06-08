package com.project.user_feed_personalization.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class UserInteraction {
    @Id
    private String id;

    @DBRef
    @Indexed
    private User user;

    @DBRef
    private Article article;

    private InteractionType type;
    private Duration readTime;
    private LocalDateTime timeStamp;

    private String sourceContext; // FEED, TAG_FILTER
    private Integer scrollPosition;

    public enum InteractionType {
        VIEW(1.0),
        LONG_PRESS(2.0),
        READ_MORE(5.0),
        LIKE(3.0),
        SHARE(4.0),
        DWELL_TIME(0.1);// Per second of reading time

        private final double weight;

        InteractionType(double weight){
            this.weight = weight;
        }

        public double getWeight(){
            return weight;
        }
    }
}

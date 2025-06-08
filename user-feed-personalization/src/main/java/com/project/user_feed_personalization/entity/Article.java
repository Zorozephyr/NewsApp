package com.project.user_feed_personalization.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "articles")
public class Article {

    public enum ProcessingStatus{
        PENDING, PROCESSING, COMPLETED, FAILED
    }
    @Id
    private String id;

    private String originalNewsId;

    @Indexed
    private String title;

    @Indexed
    private String sourceName;

    private String author;

    @TextIndexed
    private String originalDescription;

    @TextIndexed
    private String originalContent;

    @TextIndexed
    private String sourceUrl;

    private LocalDateTime publishedAt;

    @TextIndexed
    private String enrichedContent;

    @TextIndexed
    private String summary;

    @Indexed(unique = true)
    private String contentHash;

    private String semanticFingerprint;

    @DBRef
    private List<Tag> tags;

    private List<String> rawTags;

    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.PENDING;

    private String processingError;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime processedAt;

}

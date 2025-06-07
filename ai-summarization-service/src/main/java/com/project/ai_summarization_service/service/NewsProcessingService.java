package com.project.ai_summarization_service.service;

import com.project.ai_summarization_service.dto.NewsEvent;
import com.project.ai_summarization_service.entity.Article;
import com.project.ai_summarization_service.entity.Tag;
import com.project.ai_summarization_service.repository.ArticleRepository;
import com.project.ai_summarization_service.service.gpt.GPTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NewsProcessingService {
    private ArticleProcessingService articleProcessingService;
    private GPTProcessor gptProcessor;

    private ArticleRepository articleRepository;

    @Autowired
    public NewsProcessingService(ArticleProcessingService articleProcessingService, GPTProcessor gptProcessor, ArticleRepository articleRepository) {
        this.articleProcessingService = articleProcessingService;
        this.gptProcessor = gptProcessor;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void processNewsEvent(NewsEvent newsEvent){
        log.info("Starting processing for news event: {}", newsEvent.getId());

        try{
            if((articleRepository.findByOriginalNewsId(newsEvent.getId())).isPresent()) {
                log.info("News Event Already processed, skipping", newsEvent.getId());
                return;
            }

            String contentHash = articleProcessingService.generateContentHash(newsEvent.getTitle(), newsEvent.getContent(), newsEvent.getSourceName());

            String semanticFingerprint = articleProcessingService.generateSemanticFingerPrint(newsEvent.getTitle(), newsEvent.getContent());

            if(articleProcessingService.isSemanticallySimilar(contentHash,semanticFingerprint)){
                log.info("Similar article already exist for news: {}, skipping processing", newsEvent.getTitle());
                saveDuplicateReference(newsEvent, contentHash, semanticFingerprint);
                return;
            }

            Article article = createInitialArticle(newsEvent, contentHash, semanticFingerprint);
            article = articleRepository.save(article);
            log.info("Created inital article record with the ID: {}", article.getId());

            log.info("Extracting Tags using GPT...");
            List<String> rawTags = gptProcessor.generateTags(newsEvent.getContent(), newsEvent.getUrl());
            log.info("Extracted {} tags: {}", rawTags.size(), rawTags);

            log.info("🤖 Creating summary using GPT...");
            String summary = gptProcessor.summarize(newsEvent.getContent(), newsEvent.getUrl());
            log.info("✅ Created summary: {}", summary.substring(0, Math.min(100, summary.length())) + "...");

            // Step 7: AI Processing - Enrich content
            log.info("🤖 Enriching content using GPT...");
            String enrichedContent = gptProcessor.enrichedContent(newsEvent.getContent(), newsEvent.getUrl());
            log.info("✅ Enriched content length: {} characters", enrichedContent.length());

            // Step 8: Create or get tags from database
            log.info("🏷️ Processing tags in database...");
            List<Tag> tags = articleProcessingService.createOrGetTags(rawTags);
            log.info("✅ Processed {} tags in database", tags.size());

            article.setRawTags(rawTags);
            article.setTags(tags);
            article.setSummary(summary);
            article.setEnrichedContent(enrichedContent);
            article.setStatus(Article.ProcessingStatus.COMPLETED);
            article.setProcessedAt(LocalDateTime.now());

            // Step 10: Save final article
            article = articleRepository.save(article);
            log.info("✅ Successfully completed processing for article ID: {}", article.getId());

            // Step 11: Log processing summary
            logProcessingSummary(article, rawTags.size(), tags.size());
        }
        catch (Exception e) {
            log.error("❌ Error processing news event: {}", newsEvent.getId(), e);
            handleProcessingError(newsEvent, e);
        }
    }

    private Article createInitialArticle(NewsEvent newsEvent, String contentHash, String semanticFingerprint) {
        return Article.builder()
                .originalNewsId(newsEvent.getId())
                .title(newsEvent.getTitle())
                .sourceName(newsEvent.getSourceName())
                .author(newsEvent.getAuthor())
                .originalDescription(newsEvent.getDescription())
                .originalContent(newsEvent.getContent())
                .sourceUrl(newsEvent.getUrl())
                .publishedAt(newsEvent.getPublishedAt())
                .contentHash(contentHash)
                .semanticFingerprint(semanticFingerprint)
                .status(Article.ProcessingStatus.PROCESSING)
                .build();
    }

    /**
     * Save reference to duplicate article for tracking
     */
    private void saveDuplicateReference(NewsEvent newsEvent, String contentHash, String semanticFingerprint) {
        Article duplicateRef = Article.builder()
                .originalNewsId(newsEvent.getId())
                .title(newsEvent.getTitle() + " [DUPLICATE]")
                .sourceName(newsEvent.getSourceName())
                .sourceUrl(newsEvent.getUrl())
                .publishedAt(newsEvent.getPublishedAt())
                .contentHash(contentHash)
                .semanticFingerprint(semanticFingerprint)
                .status(Article.ProcessingStatus.COMPLETED)
                .processingError("Duplicate content - similar article already exists")
                .processedAt(LocalDateTime.now())
                .build();

        articleRepository.save(duplicateRef);
        log.info("📋 Saved duplicate reference for tracking: {}", duplicateRef.getId());
    }

    /**
     * Handle processing errors
     */
    private void handleProcessingError(NewsEvent newsEvent, Exception error) {
        try {
            // Try to find the article record if it was created
            articleRepository.findByOriginalNewsId(newsEvent.getId())
                    .ifPresentOrElse(
                            article -> {
                                article.setStatus(Article.ProcessingStatus.FAILED);
                                article.setProcessingError(error.getMessage());
                                article.setProcessedAt(LocalDateTime.now());
                                articleRepository.save(article);
                                log.info("💾 Updated article status to FAILED: {}", article.getId());
                            },
                            () -> {
                                // Create failed record for tracking
                                Article failedArticle = Article.builder()
                                        .originalNewsId(newsEvent.getId())
                                        .title(newsEvent.getTitle())
                                        .sourceName(newsEvent.getSourceName())
                                        .sourceUrl(newsEvent.getUrl())
                                        .publishedAt(newsEvent.getPublishedAt())
                                        .status(Article.ProcessingStatus.FAILED)
                                        .processingError(error.getMessage())
                                        .processedAt(LocalDateTime.now())
                                        .build();

                                articleRepository.save(failedArticle);
                                log.info("💾 Created failed article record: {}", failedArticle.getId());
                            }
                    );
        } catch (Exception saveError) {
            log.error("❌ Error saving failed article state", saveError);
        }
    }

    private void logProcessingSummary(Article article, int rawTagCount, int dbTagCount) {
        log.info("📊 === PROCESSING SUMMARY ===");
        log.info("Article ID: {}", article.getId());
        log.info("Original News ID: {}", article.getOriginalNewsId());
        log.info("Title: {}", article.getTitle());
        log.info("Source: {}", article.getSourceName());
        log.info("Author: {}", article.getAuthor());
        log.info("Published: {}", article.getPublishedAt());
        log.info("Summary Length: {} characters", article.getSummary() != null ? article.getSummary().length() : 0);
        log.info("Enriched Content Length: {} characters", article.getEnrichedContent() != null ? article.getEnrichedContent().length() : 0);
        log.info("Raw Tags Extracted: {}", rawTagCount);
        log.info("DB Tags Created/Updated: {}", dbTagCount);
        log.info("Processing Status: {}", article.getStatus());
        log.info("Content Hash: {}", article.getContentHash().substring(0, 16) + "...");
        log.info("Processing Completed At: {}", article.getProcessedAt());
        log.info("=== END SUMMARY ===");
    }
}

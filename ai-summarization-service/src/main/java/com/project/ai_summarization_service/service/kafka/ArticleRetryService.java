package com.project.ai_summarization_service.service.kafka;

import com.project.ai_summarization_service.entity.Article;
import com.project.ai_summarization_service.repository.ArticleRepository;
import com.project.ai_summarization_service.service.ArticleProcessingService;
import com.project.ai_summarization_service.service.gpt.GptModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleRetryService {

    private final ArticleRepository articleRepository;
    private final ArticleProcessingService articleProcessingService;
    private final GptModel gptModel;

    @Scheduled(fixedRate = 7200000)
    public void scheduleRetryCheck(){
        log.info("Scheduled Retry Check started...");
        retryFailedArticleSync();
        log.info("Scheduled Retry check completed(Async processing started");
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> retryFailedArticleSync(){
        log.info("🔄 Starting async retry process for failed articles...");

        List<Article> failedArticles = articleRepository.findByStatus(Article.ProcessingStatus.FAILED);

        if (failedArticles.isEmpty()) {
            log.info("✅ No failed articles to retry");
            return CompletableFuture.completedFuture(null);
        }

        log.info("Found {} failed articles to retry", failedArticles.size());

        List<CompletableFuture<Void>> retryTasks = failedArticles.stream().filter(article -> shouldRetryArticle(article))
                .map(this::reprocessArticleAsync)
                .toList();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(retryTasks.toArray(new CompletableFuture[0]));

        return  allTasks.thenRun(()->log.info("All Retry Tasks Completed"));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> reprocessArticleAsync(Article failedArticle) {
        log.info("🔄 Retrying article in thread {}: {}",
                Thread.currentThread().getName(), failedArticle.getId());

        try {
            // Mark as processing
            failedArticle.setStatus(Article.ProcessingStatus.PROCESSING);
            failedArticle.setProcessingError(null);
            articleRepository.save(failedArticle);

            // Retry AI processing
            retryAIProcessing(failedArticle);

            // Mark as completed
            failedArticle.setStatus(Article.ProcessingStatus.COMPLETED);
            failedArticle.setProcessedAt(LocalDateTime.now());
            articleRepository.save(failedArticle);

            log.info("✅ Successfully retried article: {}", failedArticle.getId());

        } catch (Exception e) {
            log.error("❌ Retry failed for article {}: {}", failedArticle.getId(), e.getMessage());

            // Mark as failed again
            failedArticle.setStatus(Article.ProcessingStatus.FAILED);
            failedArticle.setProcessingError("Retry failed: " + e.getMessage());
            failedArticle.setProcessedAt(LocalDateTime.now());
            articleRepository.save(failedArticle);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Retry the AI processing parts that might have failed
     */
    private void retryAIProcessing(Article article) throws Exception {
        String content = article.getOriginalContent();
        String url = article.getSourceUrl();

        // Retry missing AI components
        if (article.getRawTags() == null || article.getRawTags().isEmpty()) {
            log.info("🤖 Retrying tag extraction...");
            List<String> rawTags = gptModel.extractTags(content, url);
            article.setRawTags(rawTags);

            List<com.project.ai_summarization_service.entity.Tag> tags =
                    articleProcessingService.createOrGetTags(rawTags);
            article.setTags(tags);
        }

        if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
            log.info("🤖 Retrying summary creation...");
            String summary = gptModel.createSummary(content, url);
            article.setSummary(summary);
        }

        if (article.getEnrichedContent() == null || article.getEnrichedContent().trim().isEmpty()) {
            log.info("🤖 Retrying content enrichment...");
            String enrichedContent = gptModel.enrichContent(content, url);
            article.setEnrichedContent(enrichedContent);
        }
    }


    private boolean shouldRetryArticle(Article article) {
        // Only retry articles that failed more than 30 minutes ago
        return article.getProcessedAt() != null &&
                article.getProcessedAt().isBefore(LocalDateTime.now().minusMinutes(30)) && article.getProcessedAt().isAfter(LocalDateTime.now().minusHours(24));
    }
}

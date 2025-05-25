package com.project.ai_summarization_service.repository;

import com.project.ai_summarization_service.entity.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends MongoRepository<Article,String> {
    Optional<Article> findByContentHash(String contentHash);
    Optional<Article> findByOriginalNewsId(String originalNewsId);

    @Query("{'semanticFingerPrint': {$regex: ?0}}")
    List<Article> findBySimilarSemanticFingerPrint(String fingerprint);

    List<Article> findByStatus(Article.ProcessingStatus status);

    @Query("{'tags': ?0}")
    List<Article> findByTag(String tagId);
}

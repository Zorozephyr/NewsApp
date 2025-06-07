package com.project.ai_summarization_service.service;

import com.project.ai_summarization_service.entity.Article;
import com.project.ai_summarization_service.entity.Tag;
import com.project.ai_summarization_service.repository.ArticleRepository;
import com.project.ai_summarization_service.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleProcessingService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ArticleProcessingService(ArticleRepository articleRepository, TagRepository tagRepository) {
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Generate content hash for semantic uniqueness check
     */
    public String generateContentHash(String title, String content, String sourceName){
        try{
            String combined = String.format("%s|%s|%s",
                    normalizeText(title),
                    extractKeyWords(content),
                    sourceName
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (Exception e){
            log.error("Error generating hash",e);
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Generate semantic fingerprint for similarity detection
     */
    public String generateSemanticFingerPrint(String title, String content){
        Set<String> keywords = new HashSet<>();
        keywords.addAll(Arrays.asList(normalizeText(title).split("\\s+")));
        keywords.addAll(extractKeyPhrases(content));
        return keywords.stream().sorted().collect(Collectors.joining("|"));
    }

    /**
     * Check if article is semantically unique
     */
    public boolean isSemanticallySimilar(String contentHash, String semanticFingerprint) {
        // First check exact content hash (only catches identical republishing)
        if (articleRepository.findByContentHash(contentHash).isPresent()) {
            return true;
        }

        // Semantic similarity check using keyword overlap
        List<Article> allArticles = articleRepository.findAll(); // In production, add date filtering

        for (Article existingArticle : allArticles) {
            if (calculateSemanticSimilarity(semanticFingerprint, existingArticle.getSemanticFingerprint()) > 0.8) {
                return true; // 80% similarity threshold
            }
        }

        return false;
    }

    /*
    * Calculate sematic similarity between 2 fingerprints
     */
    private double calculateSemanticSimilarity(String fingerprint1, String fingerprint2) {
        if (fingerprint1 == null || fingerprint2 == null) {
            return 0.0;
        }

        Set<String> keywords1 = new HashSet<>(Arrays.asList(fingerprint1.split("\\|")));
        Set<String> keywords2 = new HashSet<>(Arrays.asList(fingerprint2.split("\\|")));

        // Calculate Jaccard similarity: intersection / union
        Set<String> intersection = new HashSet<>(keywords1);
        intersection.retainAll(keywords2);

        Set<String> union = new HashSet<>(keywords1);
        union.addAll(keywords2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    @Transactional
    public List<Tag> createOrGetTags(List<String> rawTagNames){
        List<Tag> result = new ArrayList<>();
        for(String rawTag: rawTagNames){
            String normalizedName = normalizeTagName(rawTag);
            String displayName = rawTag.trim();

            Optional<Tag> existingTag = tagRepository.findByName(normalizedName);

            if(existingTag.isPresent()){
                Tag tag = existingTag.get();
                tag.setUsageCount(tag.getUsageCount()+1);
                result.add(tagRepository.save(tag));
            }
            else{
                Tag newTag = Tag.builder()
                        .name(normalizedName)
                        .displayName(displayName)
                        .usageCount(1L)
                        .build();
                result.add(tagRepository.save(newTag));
            }
        }
        return result;
    }

    public String normalizeText(String text){
        return text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]","")
                .replaceAll("\\s+"," ")
                .trim();
    }
    public String normalizeTagName(String tag){
        return tag.toLowerCase().trim().replaceAll("\\s+","_");
    }
    public String extractKeyWords(String content){
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> word.length() > 4)
                .map(String::toLowerCase)
                .distinct()
                .limit(20)
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public Set<String> extractKeyPhrases(String content){
        Set<String> phrases = new HashSet<>();
        String[] sentences = content.split("[.!?]");

        for(String sentence: sentences){
            String[] words = sentence.trim().split("\\s+");
            if(words.length>=2 && words.length<=4){
                phrases.add(String.join(" ",words).toLowerCase());
            }
        }
        return phrases;
    }
}

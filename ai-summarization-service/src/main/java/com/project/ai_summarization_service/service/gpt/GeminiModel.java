package com.project.ai_summarization_service.service.gpt;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@NoArgsConstructor
public class GeminiModel implements GptModel{

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.model}")
    private String geminiApiModel;
    private Client geminiClient;


    @PostConstruct
    public void initializeClient() {
        if (!StringUtils.hasText(geminiApiKey)) {
            throw new IllegalStateException("Gemini API key is not configured");
        }
        this.geminiClient = Client.builder().apiKey(geminiApiKey).build();
        log.info("Gemini client initialized successfully with model: {}", geminiApiModel);
    }

    @Override
    public List<String> extractTags(String newsContent, String newsUrl) {
        String prompt = String.format(
                "Analyze the following news content and extract relevant tags. Return only a comma-separated list of tags covering these categories: sentiment (positive/negative/neutral), news category (politics/business/technology/sports/entertainment/health/science/world/crime/education/environment), key entities (people/organizations/locations), countries mentioned, main keywords, and topic themes. Provide 10-15 most relevant tags total.\n\n"+
                        "News Content: %s\n"+
                        "Source URL: %s\n\n"+
                        "Tags:",
                newsContent, newsUrl
        );
        try{
            GenerateContentResponse response = geminiClient.models.generateContent(
                    geminiApiModel,
                    prompt,
                    null
            );
            String result = response.text().trim();

            // Split by comma and clean up the tags
            List<String> tags = Arrays.stream(result.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());

            return tags;
        }
        catch (Exception e){
            log.error("Error extracting news tags: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public String createSummary(String newsContent, String newsUrl) {
        String prompt = String.format(
                "Create a concise 2-3 sentence summary of the following news content. Focus on the most important facts: who, what, when, where, and why. Present only the summary without any introductory text or explanations.\n\n"+
                        "News Content: %s\n"+
                        "Source URL: %s\n\n"+
                        "Summary:",
                newsContent, newsUrl
        );
        try{
            GenerateContentResponse response = geminiClient.models.generateContent(
                    geminiApiModel,
                    prompt,
                    null
            );
            String result = response.text().trim();
            return result;
        }
        catch (Exception e){
            log.error("Error creating news summary: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String enrichContent(String newsContent, String newsUrl) {
        String prompt = String.format(
                "Write a comprehensive 300-word article about the news topic below. Research the provided URL and related coverage from major news sources to gather complete information. Present only the final article content with key details, context, and insights in a neutral journalistic tone. Do not include research notes, source citations, or explanatory text about your process.\n\n"+
                        "News Content: %s\n"+
                        "Source URL: %s\n\n"+
                        "Article:",
                newsContent, newsUrl
        );
        try{
            GenerateContentResponse response = geminiClient.models.generateContent(
                    geminiApiModel,
                    prompt,
                    null
            );
            String result = response.text();
            return result;
        }
        catch (Exception e){
            log.error("Error generating enriched content");
            return null;
        }
    }
}

package com.project.ai_summarization_service.service.gpt;

public interface GPTProcessor {

    String[] generateTags(String newsContent);
    String summarize(String newsContent);

    String enrichedContent(String newsContent, String newsUrl);
}

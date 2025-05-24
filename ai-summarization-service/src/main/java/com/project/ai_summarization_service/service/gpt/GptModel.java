package com.project.ai_summarization_service.service.gpt;

public interface GptModel {
    String[] extractTags(String newsContent);

    String createSummary(String newsContent);

    String enrichContent(String newsContent, String newsUrl);

}

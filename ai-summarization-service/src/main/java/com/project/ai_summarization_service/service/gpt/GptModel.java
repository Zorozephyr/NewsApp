package com.project.ai_summarization_service.service.gpt;

import java.util.List;

public interface GptModel {
    List<String> extractTags(String newsContent, String newsUrl);

    String createSummary(String newsContent, String newsUrl);

    String enrichContent(String newsContent, String newsUrl);

}

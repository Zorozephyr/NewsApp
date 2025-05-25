package com.project.ai_summarization_service.service.gpt;

import java.util.List;

public interface GPTProcessor {

    List<String> generateTags(String newsContent, String newsUrl);
    String summarize(String newsContent, String newsUrl);

    String enrichedContent(String newsContent, String newsUrl);
}

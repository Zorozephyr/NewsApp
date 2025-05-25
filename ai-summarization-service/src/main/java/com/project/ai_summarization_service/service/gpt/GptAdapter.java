package com.project.ai_summarization_service.service.gpt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GptAdapter implements GPTProcessor{

    private final GptModel gptModel;

    @Autowired
    public GptAdapter(GptModel gptModel){
        this.gptModel=gptModel;
    }

    @Override
    public List<String> generateTags(String newsContent, String newsUrl) {
        return gptModel.extractTags(newsContent,newsUrl);
    }

    @Override
    public String summarize(String newsContent, String newsUrl) {
        return gptModel.createSummary(newsContent,newsUrl);
    }

    @Override
    public String enrichedContent(String newsContent, String newsUrl){
        return gptModel.enrichContent(newsContent,newsUrl);
    }
}

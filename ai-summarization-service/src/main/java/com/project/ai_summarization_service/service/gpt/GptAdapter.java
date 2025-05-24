package com.project.ai_summarization_service.service.gpt;

public class GptAdapter implements GPTProcessor{
    private final GptModel gptModel;

    public GptAdapter(GptModel gptModel){
        this.gptModel=gptModel;
    }

    @Override
    public String[] generateTags(String newsContent) {
        return gptModel.extractTags(newsContent);
    }

    @Override
    public String summarize(String newsContent) {
        return gptModel.createSummary(newsContent);
    }

    @Override
    public String enrichedContent(String newsContent, String newsUrl){
        return gptModel.enrichContent(newsContent,newsUrl);
    }
}

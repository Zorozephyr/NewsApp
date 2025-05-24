package com.project.ai_summarization_service.service.gpt;

import org.springframework.beans.factory.annotation.Value;

public class GeminiModel implements GptModel{

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.model}")
    private String geminiApiModel;
    private final Client geminiClient;

    public GeminiModel(){
        this.geminiClient = new Client();
    }

    private String callGeminiApi(String prompt, int maxTokens){

    }
    @Override
    public String[] extractTags(String newsContent) {
        String prompt = String.format()
    }

    @Override
    public String createSummary(String newsContent) {
        return null;
    }

    @Override
    public String enrichContent(String newsContent, String newsUrl) {
        String prompt = String.format(
                "Create a descriptive content of almost 300 words based on the following news content. Search for the news in web and"+
                        "find out more from every major news website using the news url I have given below and make sure that it is completely related to the following news content. Guarentee that "+
                        "Include key details, context and insights while maintaining a neutral tone:\n"+
                        "Content: %s\nURL: %s", newsContent, newsUrl
        );
        String response = callGeminiApi(prompt, 300);
        return response;
    }
}

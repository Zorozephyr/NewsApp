package com.project.newsprocessor.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebScraper {

    public static String scrapeFullArticleContent(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

            // This selector might need adjustment based on the article's layout.
            Elements paragraphs = doc.select("article p");

            StringBuilder fullContent = new StringBuilder();
            for (var paragraph : paragraphs) {
                fullContent.append(paragraph.text()).append("\n");
            }

            return fullContent.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to scrape content.";
        }
    }
}

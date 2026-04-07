package com.buzz.news.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArticleScraper {

    public String scrapeArticle(String sourceUrl) {

        try {
            Document document = Jsoup.connect(sourceUrl).get();
            Elements paragraphs = document.select("p");
            List<String> contentParts = new ArrayList<>();

            for (Element paragraph : paragraphs) {
                contentParts.add(paragraph.text());
            }

            return String.join(" ", contentParts);
        } catch (IOException e) {
            throw new RuntimeException("Haber içeriği çekilemedi: " + sourceUrl, e);
        }
    }
}

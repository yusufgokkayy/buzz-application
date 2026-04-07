package com.buzz.news.service.impl;

import com.buzz.news.service.SummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SummaryServiceImpl implements SummaryService {

    @Value("${ollama.model}")
    private String ollamaModel;

    @Value("${summary.max-words}")
    private int maxWords;

    @Value("${summary.min-words}")
    private int minWords;

    @Value("${summary.max-title-words}")
    private int maxTitleWords;

    @Value("${summary.min-title-words}")
    private int minTitleWords;

    private final WebClient webClient;

    public SummaryServiceImpl(WebClient ollamaWebClient) {
        this.webClient = ollamaWebClient;
    }

    @Override
    public String generateSummary(String newsBody) {
        if (newsBody == null || newsBody.isBlank()) return "";

        String prompt = """
        Görev:
        Aşağıdaki haberi özetle.
        
        Kurallar:
        1. Önce bir başlık yaz.
        2. Başlık %d-%d kelime olmalı ve haberin sonucunu tek cümlede anlatmalı.
        3. Başlığın altına haber özetini yaz.
        4. Özet %d-%d kelime olmalı.
        5. Cümleler mantıklı ve akıcı bir sırada olmalı.
        6. Gereksiz yorum ekleme, sadece haberi özetle.
        
        Haber:
        %s
        """.formatted(minTitleWords, maxTitleWords, minWords, maxWords, newsBody);

        Map<String, Object> options = new HashMap<>();
        options.put("num_ctx", 2048);
        options.put("temperature", 0.2);
        options.put("top_p", 0.9);
        options.put("num_predict", 200);

        Map<String, Object> body = new HashMap<>();
        body.put("model", ollamaModel);
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("think", false);
        body.put("options", options);

        Map response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();


        if (response == null || response.get("response") == null) {
            throw new RuntimeException("Ollama'dan özet alınamadı!");
        }

//        // Tüm response'u logla
//        System.out.println("=== OLLAMA RAW RESPONSE ===");
//        response.forEach((key, value) -> System.out.println(key + " : " + value));
//        System.out.println("===========================");

        log.debug("Ollama yanıt süresi: {}ms", response.get("total_duration"));

        String result = (String) response.get("response");
        return cleanThinkingOutput(result);
    }

    private String cleanThinkingOutput(String response) {
        if (response == null) return "";

        return response.replaceAll("(?s)<think>.*?</think>", "").strip();
    }
}

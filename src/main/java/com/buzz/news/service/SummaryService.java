package com.buzz.news.service;

public interface SummaryService {

    /**
     * Haber gövdesini alıp özetini döndürür.
     * @param newsBody Ham ve temizlenmiş haber metni
     * @return 500 karakteri geçmeyen sade özet
     */

    String generateSummary(String newsBody);
}

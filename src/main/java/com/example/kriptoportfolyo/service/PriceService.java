package com.example.kriptoportfolyo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ücretsiz CoinGecko API'sini kullanarak anlık fiyat çekme servisi.
 */
@Service
public class PriceService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PriceService() {
        this.webClient = WebClient.create("https://api.coingecko.com/api/v3");
        this.objectMapper = new ObjectMapper();
    }

    /**
     * CoinGecko ID listesine göre anlık USD fiyatlarını çeker.
     * @param coinIds Virgülle ayrılmış coingecko ID'leri (örn: "bitcoin,ethereum")
     * @return Map<CoinId, Fiyat>
     */
    public Map<String, BigDecimal> getPrices(List<String> coinIds) {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        
        if (coinIds == null || coinIds.isEmpty()) {
            return priceMap;
        }

        String idsParam = String.join(",", coinIds);

        try {
            // WebClient ile asenkron olmayan (block) basit bir çağrı yapıyoruz
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/simple/price")
                            .queryParam("ids", idsParam)
                            .queryParam("vs_currencies", "usd")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Senkron çalışması için block()

            if (response != null) {
                JsonNode rootNode = objectMapper.readTree(response);
                for (String id : coinIds) {
                    if (rootNode.has(id)) {
                        double price = rootNode.get(id).get("usd").asDouble();
                        priceMap.put(id, BigDecimal.valueOf(price));
                    } else {
                        // Fiyat bulunamazsa fallback olarak 0 ata
                        priceMap.put(id, BigDecimal.ZERO);
                    }
                }
            }
        } catch (Exception e) {
            // API hatası (Rate limit vb.) durumunda console'a yaz, fiyatları 0 dön
            System.err.println("Fiyat çekilirken hata oluştu: " + e.getMessage());
            for (String id : coinIds) {
                priceMap.put(id, BigDecimal.ZERO);
            }
        }

        return priceMap;
    }
}

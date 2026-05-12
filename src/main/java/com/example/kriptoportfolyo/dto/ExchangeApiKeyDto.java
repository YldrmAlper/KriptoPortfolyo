package com.example.kriptoportfolyo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Borsa API Anahtarları DTO.
 * Kullanıcıdan API Key ve Secret Key bilgilerini almak için kullanılır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeApiKeyDto {
    
    private Long id;

    @NotBlank(message = "Lütfen bir borsa seçin (Örn: BINANCE)")
    private String exchangeName;

    @NotBlank(message = "API Key boş olamaz")
    private String apiKey;

    @NotBlank(message = "Secret Key boş olamaz")
    private String apiSecret;
    
    private Boolean active;
}

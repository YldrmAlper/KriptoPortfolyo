package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.ExchangeApiKeyDto;
import com.example.kriptoportfolyo.entity.ExchangeApiKey;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.ExchangeApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Borsa API Anahtarları servisi.
 */
@Service
public class ExchangeApiKeyService {

    private final ExchangeApiKeyRepository apiKeyRepository;
    private final UserService userService;

    public ExchangeApiKeyService(ExchangeApiKeyRepository apiKeyRepository, UserService userService) {
        this.apiKeyRepository = apiKeyRepository;
        this.userService = userService;
    }

    /**
     * Kullanıcının tüm API anahtarlarını listeler.
     */
    public List<ExchangeApiKeyDto> getUserApiKeys(Long userId) {
        return apiKeyRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Yeni API Key ekler veya var olanı günceller.
     * Aynı borsaya ait tek bir kayıt olmasını sağlar.
     */
    @Transactional
    public void saveApiKey(ExchangeApiKeyDto dto, Long userId) {
        User user = userService.findById(userId);

        Optional<ExchangeApiKey> existingKey = apiKeyRepository.findByUserIdAndExchangeName(userId, dto.getExchangeName().toUpperCase());

        ExchangeApiKey apiKey = existingKey.orElse(new ExchangeApiKey());
        apiKey.setUser(user);
        apiKey.setExchangeName(dto.getExchangeName().toUpperCase());
        apiKey.setApiKey(dto.getApiKey());
        apiKey.setApiSecret(dto.getApiSecret());
        apiKey.setActive(true);

        apiKeyRepository.save(apiKey);
    }

    /**
     * API Key siler.
     */
    @Transactional
    public void deleteApiKey(Long id, Long userId) {
        ExchangeApiKey apiKey = apiKeyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("API Anahtarı bulunamadı"));
        apiKeyRepository.delete(apiKey);
    }

    private ExchangeApiKeyDto convertToDto(ExchangeApiKey entity) {
        ExchangeApiKeyDto dto = new ExchangeApiKeyDto();
        dto.setId(entity.getId());
        dto.setExchangeName(entity.getExchangeName());
        dto.setApiKey(entity.getApiKey());
        // Güvenlik gereği secret'ı DTO'da tam olarak dönmüyoruz (sadece maskeli)
        dto.setApiSecret("********" + entity.getApiSecret().substring(Math.max(0, entity.getApiSecret().length() - 4)));
        dto.setActive(entity.getActive());
        return dto;
    }
}

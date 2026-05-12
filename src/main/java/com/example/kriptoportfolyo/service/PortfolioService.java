package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.PortfolioItemDto;
import com.example.kriptoportfolyo.entity.Coin;
import com.example.kriptoportfolyo.entity.Exchange;
import com.example.kriptoportfolyo.entity.PortfolioItem;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.PortfolioItemRepository;
import com.example.kriptoportfolyo.util.Base64Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Portföy varlıkları iş mantığı servisi.
 */
@Service
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final CoinService coinService;

    public PortfolioService(PortfolioItemRepository portfolioItemRepository, UserService userService,
                            ExchangeService exchangeService, CoinService coinService) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.userService = userService;
        this.exchangeService = exchangeService;
        this.coinService = coinService;
    }

    /**
     * Kullanıcının portföyündeki tüm varlıkları getirir.
     */
    public List<PortfolioItemDto> getUserPortfolioItems(Long userId) {
        return portfolioItemRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Yeni bir varlık ekler.
     */
    @Transactional
    public void addPortfolioItem(PortfolioItemDto dto, Long userId) {
        User user = userService.findById(userId);
        Exchange exchange = exchangeService.getExchangeEntity(dto.getExchangeId(), userId);
        Coin coin = coinService.getCoinEntity(dto.getCoinId(), userId);

        PortfolioItem item = new PortfolioItem();
        item.setUser(user);
        item.setExchange(exchange);
        item.setCoin(coin);
        item.setQuantity(dto.getQuantity());
        item.setCostPerUnit(dto.getCostPerUnit());
        item.setSource("MANUAL");

        portfolioItemRepository.save(item);
    }

    /**
     * Mevcut bir varlığı günceller.
     */
    @Transactional
    public void updatePortfolioItem(Long id, PortfolioItemDto dto, Long userId) {
        PortfolioItem item = portfolioItemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Portföy varlığı bulunamadı"));

        item.setQuantity(dto.getQuantity());
        item.setCostPerUnit(dto.getCostPerUnit());
        // Borsa ve coin güncellemesi genelde yapılmaz, ama gerekirse eklenebilir.

        portfolioItemRepository.save(item);
    }

    /**
     * Varlığı siler.
     */
    @Transactional
    public void deletePortfolioItem(Long id, Long userId) {
        PortfolioItem item = portfolioItemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Portföy varlığı bulunamadı"));
        portfolioItemRepository.delete(item);
    }
    
    /**
     * İsim veya sembol ile arama yapar.
     */
    public List<PortfolioItemDto> searchItems(Long userId, String query) {
        return portfolioItemRepository.searchByQuery(userId, query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Entity -> DTO dönüşümü.
     * Exchange ve Coin'e ait logo verilerini de DTO'ya taşır.
     */
    private PortfolioItemDto convertToDto(PortfolioItem entity) {
        PortfolioItemDto dto = new PortfolioItemDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setCostPerUnit(entity.getCostPerUnit());
        dto.setTotalCost(entity.getTotalCost());
        dto.setSource(entity.getSource());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Borsa bilgileri
        dto.setExchangeId(entity.getExchange().getId());
        dto.setExchangeName(entity.getExchange().getName());
        dto.setExchangeLogoBase64(Base64Util.encodeToString(entity.getExchange().getLogo()));
        dto.setExchangeLogoContentType(entity.getExchange().getLogoContentType());

        // Coin bilgileri
        dto.setCoinId(entity.getCoin().getId());
        dto.setCoinName(entity.getCoin().getName());
        dto.setCoinSymbol(entity.getCoin().getSymbol());
        dto.setCoinLogoBase64(Base64Util.encodeToString(entity.getCoin().getLogo()));
        dto.setCoinLogoContentType(entity.getCoin().getLogoContentType());

        return dto;
    }
}

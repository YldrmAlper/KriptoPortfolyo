package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.PortfolioItemDto;
import com.example.kriptoportfolyo.entity.Coin;
import com.example.kriptoportfolyo.entity.Exchange;
import com.example.kriptoportfolyo.entity.PortfolioItem;
import com.example.kriptoportfolyo.entity.Trade;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.PortfolioItemRepository;
import com.example.kriptoportfolyo.repository.TradeRepository;
import com.example.kriptoportfolyo.util.Base64Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Portföy varlıkları iş mantığı servisi.
 */
@Service
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final TradeRepository tradeRepository;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final CoinService coinService;

    public PortfolioService(PortfolioItemRepository portfolioItemRepository, TradeRepository tradeRepository,
                            UserService userService, ExchangeService exchangeService, CoinService coinService) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.tradeRepository = tradeRepository;
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
     * Yeni bir varlık ekler. Zaten varsa miktarını artırıp ortalama maliyeti günceller.
     */
    @Transactional
    public void addPortfolioItem(PortfolioItemDto dto, Long userId) {
        User user = userService.findById(userId);
        Exchange exchange = exchangeService.getExchangeEntity(dto.getExchangeId(), userId);
        Coin coin = coinService.getCoinEntity(dto.getCoinId(), userId);

        portfolioItemRepository.findByUserIdAndExchangeIdAndCoinId(userId, exchange.getId(), coin.getId())
                .ifPresentOrElse(existingItem -> {
                    // Aynı varlık varsa birleştir ve ortalama maliyet hesapla
                    java.math.BigDecimal oldQuantity = existingItem.getQuantity();
                    java.math.BigDecimal oldTotalCost = oldQuantity.multiply(existingItem.getCostPerUnit());
                    
                    java.math.BigDecimal addedQuantity = dto.getQuantity();
                    java.math.BigDecimal addedTotalCost = addedQuantity.multiply(dto.getCostPerUnit());
                    
                    java.math.BigDecimal newQuantity = oldQuantity.add(addedQuantity);
                    java.math.BigDecimal newTotalCost = oldTotalCost.add(addedTotalCost);
                    
                    java.math.BigDecimal newAvgCost = newTotalCost.divide(newQuantity, 8, java.math.RoundingMode.HALF_UP);
                    
                    existingItem.setQuantity(newQuantity);
                    existingItem.setCostPerUnit(newAvgCost);
                    
                    portfolioItemRepository.save(existingItem);
                }, () -> {
                    // Yoksa yeni oluştur
                    PortfolioItem item = new PortfolioItem();
                    item.setUser(user);
                    item.setExchange(exchange);
                    item.setCoin(coin);
                    item.setQuantity(dto.getQuantity());
                    item.setCostPerUnit(dto.getCostPerUnit());
                    item.setSource("MANUAL");

                    portfolioItemRepository.save(item);
                });
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
     * Coin satış işlemi. Miktar düşürülür ve Trade kaydı oluşturulur.
     *
     * @param itemId       portföy varlık ID'si
     * @param sellQuantity satılacak miktar
     * @param sellPrice    birim satış fiyatı
     * @param userId       kullanıcı ID'si
     * @return başarı mesajı
     */
    @Transactional
    public String sellPortfolioItem(Long itemId, BigDecimal sellQuantity, BigDecimal sellPrice, Long userId) {
        PortfolioItem item = portfolioItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Portföy varlığı bulunamadı"));

        // Mevcut miktardan fazla satılamaz
        if (sellQuantity.compareTo(item.getQuantity()) > 0) {
            throw new RuntimeException("Satış miktarı mevcut miktardan fazla olamaz! (Mevcut: " + item.getQuantity() + ")");
        }

        // Trade kaydı oluştur
        Trade trade = new Trade();
        trade.setUser(item.getUser());
        trade.setCoin(item.getCoin());
        trade.setExchange(item.getExchange());
        trade.setType("SELL");
        trade.setQuantity(sellQuantity);
        trade.setPricePerUnit(sellPrice);

        // Gerçekleşmiş Kar/Zarar: (satışFiyat - maliyetFiyat) * satışMiktarı
        BigDecimal realizedPnl = sellPrice.subtract(item.getCostPerUnit()).multiply(sellQuantity);
        trade.setRealizedPnl(realizedPnl);

        tradeRepository.save(trade);

        // Portföy miktarını düşür
        BigDecimal newQuantity = item.getQuantity().subtract(sellQuantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            // Miktar sıfırlanırsa varlığı sil
            portfolioItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            portfolioItemRepository.save(item);
        }

        String pnlText = realizedPnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sellQuantity + " adet " + item.getCoin().getSymbol() + " satıldı. Gerçekleşen Kar/Zarar: "
                + pnlText + "$" + realizedPnl.setScale(2, RoundingMode.HALF_UP);
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

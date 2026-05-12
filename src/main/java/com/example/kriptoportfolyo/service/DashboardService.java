package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.DashboardSummaryDto;
import com.example.kriptoportfolyo.dto.PortfolioItemDto;
import com.example.kriptoportfolyo.dto.PortfolioItemViewDto;
import com.example.kriptoportfolyo.entity.PortfolioItem;
import com.example.kriptoportfolyo.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dashboard verilerini hesaplayan ve birleştiren ana servis.
 */
@Service
public class DashboardService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final PriceService priceService;

    public DashboardService(PortfolioItemRepository portfolioItemRepository,
                            PriceService priceService) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.priceService = priceService;
    }

    public DashboardSummaryDto getDashboardSummary(Long userId) {
        List<PortfolioItemViewDto> allItems = new ArrayList<>();

        // 1. Manuel eklenen varlıkları al ve ViewDTO'ya çevir
        List<PortfolioItem> manualItems = portfolioItemRepository.findByUserId(userId);
        for (PortfolioItem item : manualItems) {
            PortfolioItemViewDto viewDto = new PortfolioItemViewDto();
            viewDto.setId(item.getId());
            viewDto.setCoinName(item.getCoin().getName());
            viewDto.setCoinSymbol(item.getCoin().getSymbol());
            viewDto.setExchangeName(item.getExchange().getName());
            viewDto.setSource("MANUAL");
            viewDto.setQuantity(item.getQuantity());
            viewDto.setCostPerUnit(item.getCostPerUnit());
            viewDto.setTotalCost(item.getTotalCost());
            allItems.add(viewDto);
        }

        // Sadece manuel varlıkları işliyoruz
        // 3. Tüm varlıklardaki Coin sembollerini (veya isimlerini) topla ve CoinGecko API'den fiyat iste
        // Basitlik için sembol üzerinden CoinGecko id mapping yapıyoruz (normalde veritabanından id alınır)
        Map<String, String> symbolToGeckoId = new HashMap<>();
        symbolToGeckoId.put("BTC", "bitcoin");
        symbolToGeckoId.put("ETH", "ethereum");
        symbolToGeckoId.put("SOL", "solana");
        symbolToGeckoId.put("AVAX", "avalanche-2");
        symbolToGeckoId.put("ADA", "cardano");
        
        List<String> coinIdsToFetch = allItems.stream()
                .map(item -> symbolToGeckoId.getOrDefault(item.getCoinSymbol().toUpperCase(), ""))
                .filter(id -> !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, BigDecimal> currentPrices = priceService.getPrices(coinIdsToFetch);

        // 4. Fiyatları eşleştir ve PnL (Kar/Zarar) hesapla
        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        BigDecimal totalInvestedCost = BigDecimal.ZERO;

        for (PortfolioItemViewDto item : allItems) {
            String geckoId = symbolToGeckoId.getOrDefault(item.getCoinSymbol().toUpperCase(), "");
            BigDecimal currentPrice = currentPrices.getOrDefault(geckoId, BigDecimal.ZERO);
            
            item.setCurrentPrice(currentPrice);
            
            // Eğer fiyat 0 ise hesaplamaları da 0 kabul et (API hatası vb. durumu)
            if (currentPrice.compareTo(BigDecimal.ZERO) == 0) {
                item.setCurrentValue(item.getTotalCost()); // Fiyat yoksa maliyeti değer kabul et
                item.setProfitLoss(BigDecimal.ZERO);
                item.setProfitLossPercentage(BigDecimal.ZERO);
            } else {
                BigDecimal currentValue = item.getQuantity().multiply(currentPrice);
                item.setCurrentValue(currentValue);
                
                BigDecimal pnl = currentValue.subtract(item.getTotalCost());
                item.setProfitLoss(pnl);
                
                if (item.getTotalCost().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal pnlPercentage = pnl.divide(item.getTotalCost(), 4, RoundingMode.HALF_UP)
                                                  .multiply(new BigDecimal("100"));
                    item.setProfitLossPercentage(pnlPercentage);
                } else {
                    item.setProfitLossPercentage(BigDecimal.ZERO);
                }
            }
            
            totalPortfolioValue = totalPortfolioValue.add(item.getCurrentValue());
            totalInvestedCost = totalInvestedCost.add(item.getTotalCost());
        }

        // 5. Genel toplamları hesapla
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setDetailedItems(allItems);
        summary.setTotalPortfolioValue(totalPortfolioValue);
        summary.setTotalInvestedCost(totalInvestedCost);
        
        BigDecimal totalPnl = totalPortfolioValue.subtract(totalInvestedCost);
        summary.setTotalProfitLoss(totalPnl);
        
        if (totalInvestedCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalPnlPercentage = totalPnl.divide(totalInvestedCost, 4, RoundingMode.HALF_UP)
                                                    .multiply(new BigDecimal("100"));
            summary.setTotalProfitLossPercentage(totalPnlPercentage);
        } else {
            summary.setTotalProfitLossPercentage(BigDecimal.ZERO);
        }

        summary.setTotalAssetsCount(allItems.size());
        summary.setActiveExchangesCount(0); // API kullanılmadığı için her zaman 0

        return summary;
    }
}

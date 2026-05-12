package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.DashboardSummaryDto;
import com.example.kriptoportfolyo.dto.PortfolioItemViewDto;
import com.example.kriptoportfolyo.entity.PortfolioItem;
import com.example.kriptoportfolyo.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

            // CoinGecko ID'yi doğrudan Coin entity'sinden al
            String geckoId = item.getCoin().getCoingeckoId();
            viewDto.setCoingeckoId(geckoId != null ? geckoId : "");

            allItems.add(viewDto);
        }

        // 2. Tüm benzersiz CoinGecko ID'lerini topla
        List<String> coinIdsToFetch = allItems.stream()
                .map(PortfolioItemViewDto::getCoingeckoId)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, BigDecimal> currentPrices = priceService.getPrices(coinIdsToFetch);

        // 3. Fiyatları eşleştir ve PnL (Kar/Zarar) hesapla
        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        BigDecimal totalInvestedCost = BigDecimal.ZERO;

        for (PortfolioItemViewDto item : allItems) {
            String geckoId = item.getCoingeckoId();
            BigDecimal currentPrice = currentPrices.getOrDefault(geckoId, BigDecimal.ZERO);
            
            item.setCurrentPrice(currentPrice);
            
            if (currentPrice.compareTo(BigDecimal.ZERO) == 0) {
                item.setCurrentValue(item.getTotalCost());
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

        // 4. Genel toplamları hesapla
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
        
        // Aktif (farklı) borsa sayısını hesapla
        long activeExchanges = allItems.stream()
                .map(PortfolioItemViewDto::getExchangeName)
                .distinct()
                .count();
        summary.setActiveExchangesCount((int) activeExchanges);

        return summary;
    }
}

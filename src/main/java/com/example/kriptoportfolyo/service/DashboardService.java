package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.DashboardSummaryDto;
import com.example.kriptoportfolyo.dto.PortfolioItemViewDto;
import com.example.kriptoportfolyo.entity.PortfolioItem;
import com.example.kriptoportfolyo.entity.Trade;
import com.example.kriptoportfolyo.repository.PortfolioItemRepository;
import com.example.kriptoportfolyo.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final TradeRepository tradeRepository;

    public DashboardService(PortfolioItemRepository portfolioItemRepository,
                            TradeRepository tradeRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.tradeRepository = tradeRepository;
    }

    public DashboardSummaryDto getDashboardSummary(Long userId) {
        List<PortfolioItemViewDto> allItems = new ArrayList<>();

        List<PortfolioItem> manualItems = portfolioItemRepository.findByUserId(userId);
        BigDecimal totalInvestedCost = BigDecimal.ZERO;

        for (PortfolioItem item : manualItems) {
            PortfolioItemViewDto viewDto = new PortfolioItemViewDto();
            viewDto.setId(item.getId());
            viewDto.setCoinId(item.getCoin().getId());
            viewDto.setCoinName(item.getCoin().getName());
            viewDto.setCoinSymbol(item.getCoin().getSymbol());
            viewDto.setExchangeName(item.getExchange().getName());
            viewDto.setSource("MANUAL");
            BigDecimal quantity = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
            BigDecimal costPerUnit = item.getCostPerUnit() != null ? item.getCostPerUnit() : BigDecimal.ZERO;
            BigDecimal totalCost = item.getTotalCost() != null ? item.getTotalCost() : BigDecimal.ZERO;

            viewDto.setQuantity(quantity);
            viewDto.setCostPerUnit(costPerUnit);
            viewDto.setTotalCost(totalCost);

            totalInvestedCost = totalInvestedCost.add(totalCost);
            allItems.add(viewDto);
        }

        BigDecimal totalRealizedPnl = tradeRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, "SELL")
                .stream()
                .map(t -> t.getRealizedPnl() != null ? t.getRealizedPnl() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setDetailedItems(allItems);
        summary.setTotalInvestedCost(totalInvestedCost);
        summary.setTotalRealizedPnl(totalRealizedPnl);

        summary.setTotalAssetsCount(allItems.size());

        long activeExchanges = allItems.stream()
                .map(PortfolioItemViewDto::getExchangeName)
                .distinct()
                .count();
        summary.setActiveExchangesCount((int) activeExchanges);

        return summary;
    }
}

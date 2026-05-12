package com.example.kriptoportfolyo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard genel özet verilerini taşıyan DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    
    private BigDecimal totalInvestedCost; // Toplam yatırılan maliyet
    private BigDecimal totalRealizedPnl; // Toplam Gerçekleşmiş Kar/Zarar ($) (Trade kayıtlarından)
    private int totalAssetsCount;
    private int activeExchangesCount;

    // Ayrıntılı varlık listesi (Tablo ve grafikler için)
    private List<PortfolioItemViewDto> detailedItems;
}

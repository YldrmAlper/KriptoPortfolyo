package com.example.kriptoportfolyo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Dashboard üzerinde gösterilecek birleştirilmiş varlık DTO'su.
 * Hem veritabanındaki kayıtları hem de API'den gelen anlık fiyatları içerir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemViewDto {

    private Long id;
    private String coinName;
    private String coinSymbol;
    private String exchangeName;
    private String source; // "MANUAL" veya "API"

    // Miktar ve Maliyet
    private BigDecimal quantity;
    private BigDecimal costPerUnit;
    private BigDecimal totalCost; // quantity * costPerUnit

    // Anlık Veriler (CoinGecko'dan vb. gelir)
    private BigDecimal currentPrice;
    private BigDecimal currentValue; // quantity * currentPrice

    // Kar / Zarar
    private BigDecimal profitLoss; // currentValue - totalCost
    private BigDecimal profitLossPercentage; // (profitLoss / totalCost) * 100
}

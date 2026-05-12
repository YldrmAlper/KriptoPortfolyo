package com.example.kriptoportfolyo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Dashboard üzerinde gösterilecek birleştirilmiş varlık DTO'su.
 * Veritabanındaki portföy kayıtlarını ve hesaplanmış değerleri içerir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemViewDto {

    private Long id;
    private Long coinId;
    private String coinName;
    private String coinSymbol;
    private String exchangeName;
    private String source; // "MANUAL" veya "API"

    // Miktar ve Maliyet
    private BigDecimal quantity;
    private BigDecimal costPerUnit;
    private BigDecimal totalCost; // quantity * costPerUnit
}

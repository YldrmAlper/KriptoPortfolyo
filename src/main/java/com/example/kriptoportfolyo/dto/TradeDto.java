package com.example.kriptoportfolyo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {
    private Long id;
    private String type;
    private String coinName;
    private String coinSymbol;
    private String exchangeName;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalValue;
    private BigDecimal realizedPnl;
    private LocalDateTime createdAt;
}
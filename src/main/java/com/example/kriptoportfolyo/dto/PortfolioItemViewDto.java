package com.example.kriptoportfolyo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
    private String source;
    private BigDecimal quantity;
    private BigDecimal costPerUnit;
    private BigDecimal totalCost;
}
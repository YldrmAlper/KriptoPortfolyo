package com.example.kriptoportfolyo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDto {

    private Long id;

    @NotNull(message = "Lütfen bir borsa seçin")
    private Long exchangeId;
    
    private String exchangeName;
    private String exchangeLogoBase64;
    private String exchangeLogoContentType;

    @NotNull(message = "Lütfen bir coin seçin")
    private Long coinId;
    
    private String coinName;
    private String coinSymbol;
    private String coinLogoBase64;
    private String coinLogoContentType;

    @NotNull(message = "Adet boş olamaz")
    @Min(value = 0, message = "Adet 0'dan büyük olmalıdır")
    private BigDecimal quantity;

    @NotNull(message = "Birim maliyet boş olamaz")
    @Min(value = 0, message = "Birim maliyet 0'dan büyük olmalıdır")
    private BigDecimal costPerUnit;

    private BigDecimal totalCost;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
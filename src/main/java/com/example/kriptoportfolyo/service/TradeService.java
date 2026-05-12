package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.TradeDto;
import com.example.kriptoportfolyo.entity.Trade;
import com.example.kriptoportfolyo.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * İşlem (Trade) geçmişi servisi.
 */
@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    /**
     * Kullanıcının tüm işlem geçmişini getirir.
     */
    public List<TradeDto> getUserTrades(Long userId) {
        return tradeRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Toplam gerçekleşmiş PnL hesaplar.
     */
    public BigDecimal getTotalRealizedPnl(Long userId) {
        return tradeRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, "SELL").stream()
                .map(t -> t.getRealizedPnl() != null ? t.getRealizedPnl() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TradeDto convertToDto(Trade t) {
        TradeDto dto = new TradeDto();
        dto.setId(t.getId());
        dto.setType(t.getType());
        dto.setCoinName(t.getCoin().getName());
        dto.setCoinSymbol(t.getCoin().getSymbol());
        dto.setExchangeName(t.getExchange().getName());
        dto.setQuantity(t.getQuantity());
        dto.setPricePerUnit(t.getPricePerUnit());
        dto.setTotalValue(t.getTotalValue());
        dto.setRealizedPnl(t.getRealizedPnl());
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }
}

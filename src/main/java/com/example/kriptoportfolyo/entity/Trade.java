package com.example.kriptoportfolyo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * İşlem (Trade) entity sınıfı.
 * Kullanıcının yaptığı alım/satım işlemlerini kaydeder.
 * type: "BUY" veya "SELL"
 * realizedPnl: Satış işlemlerinde gerçekleşen Kar/Zarar
 */
@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    private Exchange exchange;

    @Column(nullable = false, length = 10)
    private String type; // "BUY" veya "SELL"

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 18, scale = 8)
    private BigDecimal pricePerUnit;

    @Column(name = "total_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalValue; // quantity * pricePerUnit

    @Column(name = "realized_pnl", precision = 18, scale = 2)
    private BigDecimal realizedPnl; // Satışta: (sellPrice - costPerUnit) * quantity

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.quantity != null && this.pricePerUnit != null) {
            this.totalValue = this.quantity.multiply(this.pricePerUnit);
        }
    }
}

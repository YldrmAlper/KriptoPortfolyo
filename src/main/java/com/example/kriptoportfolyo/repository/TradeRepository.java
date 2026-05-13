package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Trade> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
}
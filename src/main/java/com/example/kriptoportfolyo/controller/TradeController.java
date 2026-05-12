package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.TradeDto;
import com.example.kriptoportfolyo.service.TradeService;
import com.example.kriptoportfolyo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * İşlem Geçmişi ve Portföy Değişim Grafiği Controller.
 */
@Controller
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;

    public TradeController(TradeService tradeService, UserService userService) {
        this.tradeService = tradeService;
        this.userService = userService;
    }

    @GetMapping
    public String tradesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<TradeDto> trades = tradeService.getUserTrades(userId);
        BigDecimal totalRealizedPnl = tradeService.getTotalRealizedPnl(userId);

        model.addAttribute("trades", trades);
        model.addAttribute("totalRealizedPnl", totalRealizedPnl);

        return "trade/list";
    }
}

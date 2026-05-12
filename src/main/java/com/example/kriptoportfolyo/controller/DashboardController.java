package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.DashboardSummaryDto;
import com.example.kriptoportfolyo.service.DashboardService;
import com.example.kriptoportfolyo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Dashboard controller sınıfı.
 * Giriş sonrası ana sayfayı ve PnL verilerini gösterir.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    /**
     * Ana dashboard sayfasını gösterir.
     * DashboardService'den dönen özet verileri modele ekler.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        
        DashboardSummaryDto summary = dashboardService.getDashboardSummary(userId);
        
        model.addAttribute("currentUser", userDetails.getUsername());
        model.addAttribute("summary", summary);
        
        return "dashboard/index";
    }

    /**
     * Genel grafik sayfasını açar.
     */
    @GetMapping("/charts")
    public String charts(Model model) {
        return "chart/index";
    }

    /**
     * TradingView detay grafiği sayfasını açar.
     */
    @GetMapping("/chart/detail")
    public String chartDetail(@RequestParam("symbol") String symbol, Model model) {
        model.addAttribute("symbol", symbol.toUpperCase());
        return "chart/detail";
    }
}

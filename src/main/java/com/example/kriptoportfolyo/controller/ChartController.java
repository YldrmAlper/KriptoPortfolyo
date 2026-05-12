package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.DashboardSummaryDto;
import com.example.kriptoportfolyo.dto.PortfolioItemViewDto;
import com.example.kriptoportfolyo.service.DashboardService;
import com.example.kriptoportfolyo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard grafik verilerini sağlayan REST Controller.
 * Chart.js tarafından JSON formatında tüketilir.
 */
@RestController
@RequestMapping("/api/charts")
public class ChartController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public ChartController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    /**
     * Portföy dağılım verilerini (Coin Adı ve Değeri) döner.
     * Doughnut (Pasta) grafiği için kullanılır.
     */
    @GetMapping("/distribution")
    public ResponseEntity<Map<String, Object>> getPortfolioDistribution(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        DashboardSummaryDto summary = dashboardService.getDashboardSummary(userId);

        List<String> labels = summary.getDetailedItems().stream()
                .map(PortfolioItemViewDto::getCoinSymbol)
                .toList();

        List<Double> data = summary.getDetailedItems().stream()
                .map(item -> item.getCurrentValue().doubleValue())
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);

        return ResponseEntity.ok(result);
    }
}

package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.PortfolioItemDto;
import com.example.kriptoportfolyo.service.PortfolioService;
import com.example.kriptoportfolyo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dinamik Arama API Controller.
 * JavaScript (AJAX) isteklerini karşılar ve JSON formatında sonuç döner.
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    public SearchController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
    }

    /**
     * Portföy içinde isim veya sembole göre arama yapar.
     */
    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioItemDto>> searchPortfolio(
            @RequestParam("q") String query,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<PortfolioItemDto> results = portfolioService.searchItems(userId, query);
        
        return ResponseEntity.ok(results);
    }
}

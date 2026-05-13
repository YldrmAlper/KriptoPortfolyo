package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.CoinDto;
import com.example.kriptoportfolyo.dto.ExchangeDto;
import com.example.kriptoportfolyo.dto.PortfolioItemDto;
import com.example.kriptoportfolyo.service.CoinService;
import com.example.kriptoportfolyo.service.ExchangeService;
import com.example.kriptoportfolyo.service.PortfolioService;
import com.example.kriptoportfolyo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ExchangeService exchangeService;
    private final CoinService coinService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, ExchangeService exchangeService,
                               CoinService coinService, UserService userService) {
        this.portfolioService = portfolioService;
        this.exchangeService = exchangeService;
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping
    public String portfolioPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = getUserId(userDetails);

        model.addAttribute("portfolioItems", portfolioService.getUserPortfolioItems(userId));
        model.addAttribute("exchanges", exchangeService.getUserExchanges(userId));
        model.addAttribute("coins", coinService.getUserCoins(userId));

        if (!model.containsAttribute("portfolioItemDto")) {
            model.addAttribute("portfolioItemDto", new PortfolioItemDto());
        }
        if (!model.containsAttribute("exchangeDto")) {
            model.addAttribute("exchangeDto", new ExchangeDto());
        }
        if (!model.containsAttribute("coinDto")) {
            model.addAttribute("coinDto", new CoinDto());
        }

        return "portfolio/list";
    }

    @PostMapping("/add")
    public String addPortfolioItem(@Valid @ModelAttribute("portfolioItemDto") PortfolioItemDto dto,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.portfolioItemDto", result);
            redirectAttributes.addFlashAttribute("portfolioItemDto", dto);
            redirectAttributes.addFlashAttribute("errorModal", "addPortfolioModal");
            return "redirect:/portfolio";
        }

        try {
            Long userId = getUserId(userDetails);
            portfolioService.addPortfolioItem(dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Varlık başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/update/{id}")
    public String updatePortfolioItem(@PathVariable Long id,
                                      @RequestParam BigDecimal quantity,
                                      @RequestParam BigDecimal costPerUnit,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(userDetails);
            PortfolioItemDto dto = new PortfolioItemDto();
            dto.setQuantity(quantity);
            dto.setCostPerUnit(costPerUnit);
            portfolioService.updatePortfolioItem(id, dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Varlık başarıyla güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/sell/{id}")
    public String sellPortfolioItem(@PathVariable Long id,
                                    @RequestParam BigDecimal sellQuantity,
                                    @RequestParam BigDecimal sellPrice,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(userDetails);
            String successMessage = portfolioService.sellPortfolioItem(id, sellQuantity, sellPrice, userId);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/exchange/add")
    public String addExchange(@Valid @ModelAttribute("exchangeDto") ExchangeDto dto,
                              BindingResult result,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exchangeDto", result);
            redirectAttributes.addFlashAttribute("exchangeDto", dto);
            redirectAttributes.addFlashAttribute("errorModal", "addExchangeModal");
            return "redirect:/portfolio";
        }

        try {
            Long userId = getUserId(userDetails);
            exchangeService.addExchange(dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Borsa başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/coin/add")
    public String addCoin(@Valid @ModelAttribute("coinDto") CoinDto dto,
                          BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coinDto", result);
            redirectAttributes.addFlashAttribute("coinDto", dto);
            redirectAttributes.addFlashAttribute("errorModal", "addCoinModal");
            return "redirect:/portfolio";
        }

        try {
            Long userId = getUserId(userDetails);
            coinService.addCoin(dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Coin başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/delete/{id}")
    public String deletePortfolioItem(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserId(userDetails);
            portfolioService.deletePortfolioItem(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Varlık başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    private Long getUserId(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).getId();
    }
}

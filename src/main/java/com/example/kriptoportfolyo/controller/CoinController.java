package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.CoinDto;
import com.example.kriptoportfolyo.service.CoinService;
import com.example.kriptoportfolyo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coins")
public class CoinController {

    private final CoinService coinService;
    private final UserService userService;

    public CoinController(CoinService coinService, UserService userService) {
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping
    public String listCoins(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        model.addAttribute("coins", coinService.getUserCoins(userId));
        
        if (!model.containsAttribute("coinDto")) {
            model.addAttribute("coinDto", new CoinDto());
        }
        return "coin/list";
    }

    @PostMapping("/add")
    public String addCoin(@Valid @ModelAttribute("coinDto") CoinDto dto,
                          BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coinDto", result);
            redirectAttributes.addFlashAttribute("coinDto", dto);
            redirectAttributes.addFlashAttribute("errorModal", "addCoinModal");
            return "redirect:/coins";
        }

        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            coinService.addCoin(dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Coin başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/coins";
    }

    @PostMapping("/update/{id}")
    public String updateCoin(@PathVariable Long id, @Valid @ModelAttribute("coinDto") CoinDto dto,
                             BindingResult result, @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Coin güncellenirken doğrulama hatası oluştu.");
            return "redirect:/coins";
        }
        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            coinService.updateCoin(id, dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Coin başarıyla güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/coins";
    }

    @PostMapping("/delete/{id}")
    public String deleteCoin(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            coinService.deleteCoin(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Coin başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/coins";
    }
}

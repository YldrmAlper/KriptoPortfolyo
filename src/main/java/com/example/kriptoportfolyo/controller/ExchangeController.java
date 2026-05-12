package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.dto.ExchangeDto;
import com.example.kriptoportfolyo.service.ExchangeService;
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
@RequestMapping("/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final UserService userService;

    public ExchangeController(ExchangeService exchangeService, UserService userService) {
        this.exchangeService = exchangeService;
        this.userService = userService;
    }

    @GetMapping
    public String listExchanges(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        model.addAttribute("exchanges", exchangeService.getUserExchanges(userId));
        
        if (!model.containsAttribute("exchangeDto")) {
            model.addAttribute("exchangeDto", new ExchangeDto());
        }
        return "exchange/list";
    }

    @PostMapping("/add")
    public String addExchange(@Valid @ModelAttribute("exchangeDto") ExchangeDto dto,
                              BindingResult result,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exchangeDto", result);
            redirectAttributes.addFlashAttribute("exchangeDto", dto);
            redirectAttributes.addFlashAttribute("errorModal", "addExchangeModal");
            return "redirect:/exchanges";
        }

        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            exchangeService.addExchange(dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Borsa başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/exchanges";
    }

    @PostMapping("/update/{id}")
    public String updateExchange(@PathVariable Long id, @Valid @ModelAttribute("exchangeDto") ExchangeDto dto,
                                 BindingResult result, @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Borsa güncellenirken doğrulama hatası oluştu.");
            return "redirect:/exchanges";
        }
        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            exchangeService.updateExchange(id, dto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Borsa başarıyla güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/exchanges";
    }

    @PostMapping("/delete/{id}")
    public String deleteExchange(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            exchangeService.deleteExchange(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Borsa başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/exchanges";
    }
}

package com.example.kriptoportfolyo.controller;

import com.example.kriptoportfolyo.entity.Coin;
import com.example.kriptoportfolyo.entity.Exchange;
import com.example.kriptoportfolyo.service.CoinService;
import com.example.kriptoportfolyo.service.ExchangeService;
import com.example.kriptoportfolyo.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Resim Controller sınıfı.
 * Veritabanında BLOB olarak saklanan resimleri dışarı sunmak için alternatif bir yöntemdir.
 * Base64 yerine doğrudan image/png olarak resim dönmek istenirse kullanılabilir.
 */
@Controller
public class ImageController {

    private final ExchangeService exchangeService;
    private final CoinService coinService;
    private final UserService userService;

    public ImageController(ExchangeService exchangeService, CoinService coinService, UserService userService) {
        this.exchangeService = exchangeService;
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping("/images/exchange/{id}")
    public ResponseEntity<byte[]> getExchangeLogo(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        Exchange exchange = exchangeService.getExchangeEntity(id, userId);

        if (exchange.getLogo() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(exchange.getLogoContentType()));
            return new ResponseEntity<>(exchange.getLogo(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/images/coin/{id}")
    public ResponseEntity<byte[]> getCoinLogo(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        Coin coin = coinService.getCoinEntity(id, userId);

        if (coin.getLogo() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(coin.getLogoContentType()));
            return new ResponseEntity<>(coin.getLogo(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

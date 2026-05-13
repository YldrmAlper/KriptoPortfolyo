package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.ExchangeDto;
import com.example.kriptoportfolyo.entity.Exchange;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.ExchangeRepository;
import com.example.kriptoportfolyo.util.Base64Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final com.example.kriptoportfolyo.repository.PortfolioItemRepository portfolioItemRepository;

    public ExchangeService(ExchangeRepository exchangeRepository, ImageService imageService, UserService userService, com.example.kriptoportfolyo.repository.PortfolioItemRepository portfolioItemRepository) {
        this.exchangeRepository = exchangeRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public List<ExchangeDto> getUserExchanges(Long userId) {
        return exchangeRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addExchange(ExchangeDto dto, Long userId) {
        User user = userService.findById(userId);
        Exchange exchange = new Exchange();
        exchange.setName(dto.getName());
        exchange.setUser(user);

        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            exchange.setLogo(imageService.getBytesFromFile(dto.getLogoFile()));
            exchange.setLogoContentType(imageService.getContentType(dto.getLogoFile()));
        }

        exchangeRepository.save(exchange);
    }

    public Exchange getExchangeEntity(Long id, Long userId) {
        return exchangeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Borsa bulunamadı"));
    }

    @Transactional
    public void updateExchange(Long id, ExchangeDto dto, Long userId) {
        Exchange exchange = getExchangeEntity(id, userId);
        exchange.setName(dto.getName());

        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            imageService.validateImage(dto.getLogoFile());
            try {
                exchange.setLogo(dto.getLogoFile().getBytes());
                exchange.setLogoContentType(dto.getLogoFile().getContentType());
            } catch (Exception e) {
                throw new RuntimeException("Logo güncellenirken hata oluştu");
            }
        }
        exchangeRepository.save(exchange);
    }

    @Transactional
    public void deleteExchange(Long id, Long userId) {
        if (portfolioItemRepository.existsByExchangeIdAndUserId(id, userId)) {
            throw new RuntimeException("Bu borsa portföyünüzde aktif olarak kullanılıyor. Lütfen önce portföyünüzdeki ilgili varlıkları silin.");
        }
        Exchange exchange = getExchangeEntity(id, userId);
        exchangeRepository.delete(exchange);
    }

    private ExchangeDto convertToDto(Exchange entity) {
        ExchangeDto dto = new ExchangeDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogoBase64(Base64Util.encodeToString(entity.getLogo()));
        dto.setLogoContentType(entity.getLogoContentType());
        return dto;
    }
}

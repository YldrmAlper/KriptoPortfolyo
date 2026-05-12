package com.example.kriptoportfolyo.service;

import com.example.kriptoportfolyo.dto.CoinDto;
import com.example.kriptoportfolyo.entity.Coin;
import com.example.kriptoportfolyo.entity.User;
import com.example.kriptoportfolyo.repository.CoinRepository;
import com.example.kriptoportfolyo.util.Base64Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Coin (Kripto Para) iş mantığı servisi.
 */
@Service
public class CoinService {

    private final CoinRepository coinRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final com.example.kriptoportfolyo.repository.PortfolioItemRepository portfolioItemRepository;

    public CoinService(CoinRepository coinRepository, ImageService imageService, UserService userService, com.example.kriptoportfolyo.repository.PortfolioItemRepository portfolioItemRepository) {
        this.coinRepository = coinRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    /**
     * Kullanıcıya ait tüm coinleri listeler.
     */
    public List<CoinDto> getUserCoins(Long userId) {
        return coinRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Yeni bir coin ekler.
     */
    @Transactional
    public void addCoin(CoinDto dto, Long userId) {
        User user = userService.findById(userId);
        Coin coin = new Coin();
        coin.setName(dto.getName());
        coin.setSymbol(dto.getSymbol().toUpperCase());
        coin.setCoingeckoId(dto.getCoingeckoId());
        coin.setUser(user);

        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            coin.setLogo(imageService.getBytesFromFile(dto.getLogoFile()));
            coin.setLogoContentType(imageService.getContentType(dto.getLogoFile()));
        }

        coinRepository.save(coin);
    }

    /**
     * ID ve Kullanıcı ID'ye göre coini getirir.
     */
    public Coin getCoinEntity(Long id, Long userId) {
        return coinRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Coin bulunamadı"));
    }

    /**
     * Coin günceller.
     */
    @Transactional
    public void updateCoin(Long id, CoinDto dto, Long userId) {
        Coin coin = getCoinEntity(id, userId);
        coin.setName(dto.getName());
        coin.setSymbol(dto.getSymbol().toUpperCase());
        coin.setCoingeckoId(dto.getCoingeckoId());
        
        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            imageService.validateImage(dto.getLogoFile());
            try {
                coin.setLogo(dto.getLogoFile().getBytes());
                coin.setLogoContentType(dto.getLogoFile().getContentType());
            } catch (Exception e) {
                throw new RuntimeException("Logo güncellenirken hata oluştu");
            }
        }
        coinRepository.save(coin);
    }

    /**
     * Coin siler. Başka tabloda kullanılıyorsa hata fırlatır.
     */
    @Transactional
    public void deleteCoin(Long id, Long userId) {
        if (portfolioItemRepository.existsByCoinIdAndUserId(id, userId)) {
            throw new RuntimeException("Bu coin portföyünüzde aktif olarak kullanılıyor. Lütfen önce portföyünüzdeki ilgili varlıkları silin.");
        }
        Coin coin = getCoinEntity(id, userId);
        coinRepository.delete(coin);
    }

    /**
     * Entity -> DTO dönüşümü.
     */
    private CoinDto convertToDto(Coin entity) {
        CoinDto dto = new CoinDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSymbol(entity.getSymbol());
        dto.setCoingeckoId(entity.getCoingeckoId());
        dto.setLogoBase64(Base64Util.encodeToString(entity.getLogo()));
        dto.setLogoContentType(entity.getLogoContentType());
        return dto;
    }
}

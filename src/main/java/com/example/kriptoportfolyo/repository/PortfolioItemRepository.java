package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Portföy varlık veritabanı erişim katmanı.
 * Kullanıcıya özel CRUD, kaynak (source) filtreleme ve dinamik arama metotları içerir.
 */
@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    /**
     * Belirli bir kullanıcıya ait tüm portföy varlıklarını listeler.
     *
     * @param userId kullanıcı ID'si
     * @return kullanıcının portföy varlıkları
     */
    List<PortfolioItem> findByUserId(Long userId);

    /**
     * Belirli bir kullanıcıya ait belirli bir portföy varlığını bulur.
     * Yetkilendirme kontrolü için kullanılır.
     *
     * @param id     varlık ID'si
     * @param userId kullanıcı ID'si
     * @return bulunan portföy varlığı (Optional)
     */
    Optional<PortfolioItem> findByIdAndUserId(Long id, Long userId);

    /**
     * Belirli bir kullanıcının belirli bir borsa ve coin'e ait varlığını bulur (Stackleme için).
     */
    Optional<PortfolioItem> findByUserIdAndExchangeIdAndCoinId(Long userId, Long exchangeId, Long coinId);

    /**
     * Kullanıcının portföyünde coin adı, sembolü veya borsa adına göre arama yapar.
     * Dinamik arama özelliği için JOIN sorgusu kullanır.
     *
     * @param userId kullanıcı ID'si
     * @param query  arama terimi
     * @return eşleşen portföy varlıkları
     */
    @Query("SELECT pi FROM PortfolioItem pi " +
           "JOIN pi.coin c " +
           "JOIN pi.exchange e " +
           "WHERE pi.user.id = :userId " +
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.symbol) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<PortfolioItem> searchByQuery(@Param("userId") Long userId, @Param("query") String query);

    // Kısıtlama (Constraint) kontrolleri için
    boolean existsByExchangeIdAndUserId(Long exchangeId, Long userId);
    
    boolean existsByCoinIdAndUserId(Long coinId, Long userId);
}

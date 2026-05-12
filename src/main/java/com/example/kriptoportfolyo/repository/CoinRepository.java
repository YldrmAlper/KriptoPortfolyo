package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Coin veritabanı erişim katmanı.
 * Kullanıcıya özel coin CRUD işlemleri, isim/sembol ile arama metodu içerir.
 */
@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

    /**
     * Belirli bir kullanıcıya ait tüm coinleri listeler.
     *
     * @param userId kullanıcı ID'si
     * @return kullanıcının coinleri
     */
    List<Coin> findByUserId(Long userId);

    /**
     * Belirli bir kullanıcıya ait belirli bir coini bulur.
     * Yetkilendirme kontrolü için kullanılır.
     *
     * @param id     coin ID'si
     * @param userId kullanıcı ID'si
     * @return bulunan coin (Optional)
     */
    Optional<Coin> findByIdAndUserId(Long id, Long userId);

    /**
     * Kullanıcının coinleri arasında isim veya sembol ile arama yapar (case-insensitive).
     * Dinamik arama özelliği için kullanılır.
     *
     * @param userId kullanıcı ID'si
     * @param query  arama terimi (isim veya sembol)
     * @return eşleşen coinler
     */
    @Query("SELECT c FROM Coin c WHERE c.user.id = :userId " +
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.symbol) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Coin> searchByNameOrSymbol(@Param("userId") Long userId, @Param("query") String query);
}

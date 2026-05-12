package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Borsa veritabanı erişim katmanı.
 * Kullanıcıya özel borsa CRUD işlemleri ve arama metodu içerir.
 */
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    /**
     * Belirli bir kullanıcıya ait tüm borsaları listeler.
     *
     * @param userId kullanıcı ID'si
     * @return kullanıcının borsaları
     */
    List<Exchange> findByUserId(Long userId);

    /**
     * Belirli bir kullanıcıya ait belirli bir borsayı bulur.
     * Yetkilendirme kontrolü için kullanılır: kullanıcı sadece kendi borsalarına erişebilir.
     *
     * @param id     borsa ID'si
     * @param userId kullanıcı ID'si
     * @return bulunan borsa (Optional)
     */
    Optional<Exchange> findByIdAndUserId(Long id, Long userId);

    /**
     * Kullanıcının borsaları arasında isme göre arama yapar (case-insensitive).
     *
     * @param userId kullanıcı ID'si
     * @param query  arama terimi
     * @return eşleşen borsalar
     */
    @Query("SELECT e FROM Exchange e WHERE e.user.id = :userId " +
           "AND LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Exchange> searchByName(@Param("userId") Long userId, @Param("query") String query);
}

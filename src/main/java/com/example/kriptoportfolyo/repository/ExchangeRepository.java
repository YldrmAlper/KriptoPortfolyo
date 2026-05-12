package com.example.kriptoportfolyo.repository;

import com.example.kriptoportfolyo.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Borsa veritabanı erişim katmanı.
 * Kullanıcıya özel borsa CRUD işlemleri içerir.
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
}
